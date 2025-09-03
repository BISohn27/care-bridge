package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.Payment;
import com.donation.carebridge.payment.domain.PaymentStatus;
import com.donation.carebridge.payment.domain.pg.PgAccount;
import com.donation.carebridge.payment.domain.pg.PgProvider;
import com.donation.carebridge.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.payment.dto.CreatePaymentResult;
import com.donation.carebridge.payment.dto.NextAction;
import com.donation.carebridge.payment.config.PaymentUrlProperties;
import com.donation.carebridge.payment.dto.ProviderContext;
import com.donation.carebridge.payment.dto.ProviderSelection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentUrlProperties paymentUrlProperties;
    private final PgRouter pgRouter;
    private final IdempotencyStore<CreatePaymentResult> createIdempotencyStore;
    private final PgProviderRepository pgProviderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentExecutor paymentExecutor;

    @Transactional
    public CreatePaymentResult create(CreatePaymentRequest createRequest) {
        Optional<CreatePaymentResult> foundByIdempotencyKey =
                createIdempotencyStore.findByKey(createRequest.idempotencyKey());

        if (foundByIdempotencyKey.isPresent()) {
            var result = foundByIdempotencyKey.get();
            log.info("Idempotency key [{}] hit. Returning existing paymentId={}",
                    createRequest.idempotencyKey(), result.paymentId());
            return result;
        }

        ProviderSelection selection = pgRouter.resolve(createRequest.pgProviderCode(),
                createRequest.paymentMethod(),
                createRequest.currency(),
                createRequest.amount());

        PgProvider pgProvider =
                pgProviderRepository.findByCodeAndAccountStatus(selection.pgProviderCode(), selection.env());

        Payment created = Payment.create(
                createRequest.caseId(),
                createRequest.donorId(),
                createRequest.amount(),
                createRequest.currency(),
                createRequest.idempotencyKey(),
                pgProvider);

        paymentRepository.save(created);

        NextAction nextAction = paymentExecutor.prepareCreateSession(
                getCreatePaymentCommand(created.getId(), createRequest),
                getProviderContext(pgProvider.getAccounts()));

        CreatePaymentResult createPaymentResult = new CreatePaymentResult(created.getId(), PaymentStatus.CREATED, nextAction);
        createIdempotencyStore.save(createRequest.idempotencyKey(), createPaymentResult);
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

    private ProviderContext getProviderContext(List<PgAccount> pgAccounts) {
        PgAccount pgAccount = pgAccounts.get(0);
        return new ProviderContext(pgAccount.getClientId(), pgAccount.getApiKeyEncrypted(), pgAccount.getEnvironment());
    }
}
