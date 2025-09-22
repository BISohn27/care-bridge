package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.payment.model.PaymentStatus;

public record ConfirmPaymentResult(
    String paymentId,
    PaymentStatus status,
    Long approvedAmount
) {
}