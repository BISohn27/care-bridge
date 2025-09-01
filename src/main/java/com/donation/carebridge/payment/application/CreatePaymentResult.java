package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.PaymentStatus;

public record CreatePaymentResult(
    String paymentId,
    PaymentStatus status,
    NextAction nextAction) {
}
