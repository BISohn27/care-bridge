package com.donation.carebridge.donation.domain.payment.application.in;

import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentResult;

public interface PaymentConfirmer {

    ConfirmPaymentResult confirm(ConfirmPaymentRequest request);
}