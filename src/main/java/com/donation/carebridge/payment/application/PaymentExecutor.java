package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.pg.PgProviderCode;
import com.donation.carebridge.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.payment.dto.NextAction;
import com.donation.carebridge.payment.dto.ProviderContext;

public interface PaymentExecutor {

    PgProviderCode key();
    NextAction prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext);
}