package com.donation.carebridge.payments.domain.payment;

import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import com.donation.carebridge.payments.dto.payment.CreatePaymentCommand;
import com.donation.carebridge.payments.dto.payment.PaymentExecutionResult;
import com.donation.carebridge.payments.dto.payment.ProviderContext;

public interface PaymentExecutor {

    PgProviderCode key();
    PaymentExecutionResult prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext);
}