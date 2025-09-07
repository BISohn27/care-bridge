package com.donation.carebridge.payments.payment.application;

import com.donation.carebridge.payments.payment.annotation.IdempotencyCheck;
import com.donation.carebridge.payments.payment.config.PaymentUrlProperties;
import com.donation.carebridge.payments.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.payments.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.payments.payment.dto.CreatePaymentResult;
import com.donation.carebridge.payments.payment.dto.PaymentExecutionResult;
import com.donation.carebridge.payments.payment.dto.ProviderContext;
import com.donation.carebridge.payments.payment.dto.ProviderSelection;
import com.donation.carebridge.payments.payment.event.PaymentEventPublished;
import com.donation.carebridge.payments.payment.model.Payment;
import com.donation.carebridge.payments.payment.model.PaymentEvent;
import com.donation.carebridge.payments.payment.model.PaymentStatus;
import com.donation.carebridge.payments.payment.out.PaymentEventRepository;
import com.donation.carebridge.payments.payment.out.PaymentRepository;
import com.donation.carebridge.payments.pg.application.PgProviderService;
import com.donation.carebridge.payments.pg.model.PgAccount;
import com.donation.carebridge.payments.pg.model.PgProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

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
                createRequest.caseId(),
                createRequest.donorId(),
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

    private CreatePaymentCommand getCreatePaymentCommand(String paymentId, CreatePaymentRequest request) {
        return new CreatePaymentCommand(
                paymentId,
                request.idempotencyKey(),
                request.amount(),
                request.currency(),
                request.caseId() + " - " + request.donorId(),
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
