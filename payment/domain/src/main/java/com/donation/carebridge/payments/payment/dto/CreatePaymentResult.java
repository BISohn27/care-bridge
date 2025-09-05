package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.payment.model.PaymentStatus;

public record CreatePaymentResult(
    String paymentId,
    PaymentStatus status,
    NextAction nextAction) {
}
