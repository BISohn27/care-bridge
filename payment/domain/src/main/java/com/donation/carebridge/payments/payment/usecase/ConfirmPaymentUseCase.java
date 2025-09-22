package com.donation.carebridge.payments.payment.usecase;

import com.donation.carebridge.payments.payment.dto.ConfirmPaymentRequest;
import com.donation.carebridge.payments.payment.dto.ConfirmPaymentResult;

public interface ConfirmPaymentUseCase {

    ConfirmPaymentResult confirm(ConfirmPaymentRequest request);
}