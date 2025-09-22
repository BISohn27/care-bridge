package com.donation.carebridge.payments.payment.dto;

import java.util.Map;

public record ConfirmPaymentRequest(
    String paymentId,
    String idempotencyKey,
    Map<String, Object> payload
) {
}