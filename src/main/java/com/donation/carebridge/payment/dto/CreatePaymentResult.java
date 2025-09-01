package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payment.domain.PaymentStatus;

public record CreatePaymentResult(
    String paymentId,
    PaymentStatus status,
    NextAction nextAction) {
}
