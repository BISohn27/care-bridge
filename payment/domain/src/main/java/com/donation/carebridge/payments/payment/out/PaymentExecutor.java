package com.donation.carebridge.payments.payment.out;

import com.donation.carebridge.payments.payment.dto.ConfirmPaymentCommand;
import com.donation.carebridge.payments.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.payments.payment.dto.PaymentExecutionResult;
import com.donation.carebridge.payments.payment.dto.ProviderContext;
import com.donation.carebridge.payments.pg.model.PgProviderCode;

public interface PaymentExecutor {

    PgProviderCode key();
    PaymentExecutionResult prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext);
    PaymentExecutionResult confirmPayment(ConfirmPaymentCommand command, ProviderContext providerContext);
}