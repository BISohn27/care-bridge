package com.donation.carebridge.payments.payment.dto;

import java.util.Map;

public record ConfirmPaymentCommand(
    String paymentId,
    String idempotencyKey,
    long amount,
    Map<String, Object> providerPayload
) {
}