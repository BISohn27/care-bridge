package com.donation.carebridge.donation.domain.payment.out;

import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentCommand;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.donation.domain.payment.dto.PaymentExecutionResult;
import com.donation.carebridge.donation.domain.payment.dto.ProviderContext;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

public interface PaymentExecutor {

    PgProviderCode key();
    PaymentExecutionResult prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext);
    PaymentExecutionResult confirmPayment(ConfirmPaymentCommand command, ProviderContext providerContext);
}