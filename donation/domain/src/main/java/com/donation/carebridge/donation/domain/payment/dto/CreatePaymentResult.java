package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;

public record CreatePaymentResult(
    String paymentId,
    PaymentStatus status,
    NextAction nextAction) {
}
