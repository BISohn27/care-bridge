package com.donation.carebridge.payments.domain.payment;

import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import com.donation.carebridge.payments.dto.payment.CreatePaymentCommand;
import com.donation.carebridge.payments.dto.payment.NextAction;
import com.donation.carebridge.payments.dto.payment.ProviderContext;

public interface PaymentExecutor {

    PgProviderCode key();
    NextAction prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext);
}