package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;

public record ConfirmPaymentResult(
    String paymentId,
    PaymentStatus status,
    Long approvedAmount
) {
}