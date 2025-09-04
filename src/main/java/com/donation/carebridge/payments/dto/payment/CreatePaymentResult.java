package com.donation.carebridge.payments.dto.payment;

import com.donation.carebridge.payments.domain.payment.PaymentStatus;

public record CreatePaymentResult(
    String paymentId,
    PaymentStatus status,
    NextAction nextAction) {
}
