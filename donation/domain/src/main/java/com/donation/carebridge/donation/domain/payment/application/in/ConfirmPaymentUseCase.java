package com.donation.carebridge.donation.domain.payment.application.in;

import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentResult;

public interface ConfirmPaymentUseCase {

    ConfirmPaymentResult confirm(ConfirmPaymentRequest request);
}