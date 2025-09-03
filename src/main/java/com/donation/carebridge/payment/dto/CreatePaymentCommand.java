package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payment.domain.Currency;

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
