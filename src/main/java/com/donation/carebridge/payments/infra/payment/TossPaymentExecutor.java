package com.donation.carebridge.payments.infra.payment;

import com.donation.carebridge.payments.domain.payment.PaymentExecutor;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import com.donation.carebridge.payments.dto.payment.CreatePaymentCommand;
import com.donation.carebridge.payments.dto.payment.NextAction;
import com.donation.carebridge.payments.dto.payment.ProviderContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TossPaymentExecutor implements PaymentExecutor {

    @Override
    public PgProviderCode key() {
        return PgProviderCode.TOSS;
    }

    @Override
    public NextAction prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext) {
        return new NextAction(
                providerContext.flowType(),
                key(),
                Map.of("clientId", providerContext.clientId())
        );
    }
}
