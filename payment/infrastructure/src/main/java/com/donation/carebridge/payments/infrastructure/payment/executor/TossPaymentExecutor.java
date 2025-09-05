package com.donation.carebridge.payments.infrastructure.payment.executor;

import com.donation.carebridge.payments.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.payments.payment.dto.NextAction;
import com.donation.carebridge.payments.payment.dto.PaymentExecutionResult;
import com.donation.carebridge.payments.payment.dto.ProviderContext;
import com.donation.carebridge.payments.payment.model.PaymentExecutor;
import com.donation.carebridge.payments.pg.model.PgProviderCode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TossPaymentExecutor implements PaymentExecutor {

    @Override
    public PgProviderCode key() {
        return PgProviderCode.TOSS;
    }

    @Override
    public PaymentExecutionResult prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext) {
        return new PaymentExecutionResult(
                new NextAction(
                    providerContext.flowType(),
                    key(),
                    Map.of("clientId", providerContext.clientId())),
                null);
    }
}
