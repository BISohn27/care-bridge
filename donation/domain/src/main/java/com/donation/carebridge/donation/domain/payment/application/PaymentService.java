package com.donation.carebridge.donation.domain.payment.application;

import com.donation.carebridge.donation.domain.payment.annotation.IdempotencyCheck;
import com.donation.carebridge.donation.domain.payment.config.PaymentUrlProperties;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentCommand;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentResult;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;
import com.donation.carebridge.donation.domain.payment.dto.PaymentExecutionResult;
import com.donation.carebridge.donation.domain.payment.dto.ProviderContext;
import com.donation.carebridge.donation.domain.payment.dto.ProviderSelection;
import com.donation.carebridge.donation.domain.payment.event.PaymentEventPublished;
import com.donation.carebridge.donation.domain.payment.exception.PaymentException;
import com.donation.carebridge.donation.domain.payment.model.Payment;
import com.donation.carebridge.donation.domain.payment.model.PaymentEvent;
import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;
import com.donation.carebridge.donation.domain.payment.application.out.PaymentEventRepository;
import com.donation.carebridge.donation.domain.payment.application.out.PaymentRepository;
import com.donation.carebridge.donation.domain.payment.application.in.ConfirmPaymentUseCase;
import com.donation.carebridge.donation.domain.payment.application.in.CreatePaymentUseCase;
import com.donation.carebridge.donation.domain.pg.application.PgProviderService;
import com.donation.carebridge.donation.domain.pg.model.PgAccount;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements CreatePaymentUseCase, ConfirmPaymentUseCase {

    private final PaymentUrlProperties paymentUrlProperties;
    private final PgRouter pgRouter;
    private final PgProviderService pgProviderService;
    private final PaymentExecutorRegistry paymentExecutorRegistry;
    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @IdempotencyCheck(prefix = "payment-create")
    public CreatePaymentResult create(CreatePaymentRequest createRequest) {

        ProviderSelection selection = pgRouter.resolve(createRequest.pgProviderCode(),
                createRequest.paymentMethod(),
                createRequest.currency(),
                createRequest.amount());

        PgProvider pgProvider = pgProviderService.getProvider(selection.pgProviderCode(), selection.env());

        Payment created = Payment.create(
                createRequest.donerId(),
                createRequest.amount(),
                createRequest.currency(),
                createRequest.idempotencyKey(),
                pgProvider);

        paymentRepository.save(created);

        PaymentExecutionResult executionResult = paymentExecutorRegistry.get(pgProvider.getCode())
                .prepareCreateSession(
                        getCreatePaymentCommand(created.getId(), createRequest),
                        getProviderContext(pgProvider));

        CreatePaymentResult createPaymentResult = new CreatePaymentResult(
                created.getId(), PaymentStatus.CREATED, executionResult.nextAction());

        PaymentEvent createEvent = PaymentEvent.create(created.getId(), executionResult.rawPayload());
        paymentEventRepository.save(createEvent);
        eventPublisher.publishEvent(PaymentEventPublished.from(createEvent));

        return createPaymentResult;
    }

    @Transactional
    @IdempotencyCheck(prefix = "payment-confirm")
    public ConfirmPaymentResult confirm(ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentException("PAYMENT_NOT_FOUND", 
                        "Payment not found: " + request.paymentId()));

        PgProvider pgProvider = pgProviderService.getProvider(payment.getPgProvider(), null);
        
        PaymentExecutionResult executionResult = paymentExecutorRegistry.get(pgProvider.getCode())
                .confirmPayment(
                        new ConfirmPaymentCommand(
                                payment.getId(),
                                request.idempotencyKey(),
                                payment.getAmount(),
                                request.payload()),
                        getProviderContext(pgProvider));

        updatePaymentStatus(payment, executionResult);

        PaymentEvent confirmEvent = PaymentEvent.confirm(payment.getId(), executionResult.rawPayload());
        paymentEventRepository.save(confirmEvent);
        eventPublisher.publishEvent(PaymentEventPublished.from(confirmEvent));

        return new ConfirmPaymentResult(
                payment.getId(),
                executionResult.status(),
                payment.getAmount());
    }

    private void updatePaymentStatus(Payment payment, PaymentExecutionResult executionResult) {
        switch (executionResult.status()) {
            case PAID -> payment.markPaid(executionResult.pgPaymentId());
            case FAILED -> {
                if (executionResult.reasonCode() != null) {
                    payment.markFailedFromPg(executionResult.reasonCode(), executionResult.reasonMessage());
                } else {
                    payment.markFailedFromSystem("UNKNOWN_ERROR", "Confirm failed without specific reason");
                }
            }
            default -> throw new PaymentException("INVALID_CONFIRM_RESULT", 
                    "Unexpected status from confirm: " + executionResult.status());
        }
    }

    private CreatePaymentCommand getCreatePaymentCommand(String paymentId, CreatePaymentRequest createRequest) {
        return new CreatePaymentCommand(
                paymentId,
                createRequest.idempotencyKey(),
                createRequest.amount(),
                createRequest.currency(),
                createRequest.donerId(),
                paymentUrlProperties.getSuccessUrl(),
                paymentUrlProperties.getFailUrl(),
                paymentUrlProperties.getCancelUrl()
        );
    }

    private ProviderContext getProviderContext(PgProvider pgProvider) {
        PgAccount pgAccount = pgProvider.getAccounts().get(0);
        return new ProviderContext(
                pgProvider.getFlowType(),
                pgAccount.getClientId(),
                pgAccount.getApiKeyEncrypted(),
                pgAccount.getEnvironment());
    }
}
