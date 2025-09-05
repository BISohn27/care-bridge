package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.payment.model.Currency;

public record CreatePaymentCommand(
    String paymentId,
    String idempotencyKey,
    long amount,
    Currency currency,
    String donationName,
    String successUrl,
    String failUrl,
    String cancelUrl
) {
}
