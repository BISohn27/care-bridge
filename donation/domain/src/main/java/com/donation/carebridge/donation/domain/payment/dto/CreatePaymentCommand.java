package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.donation.domain.payment.model.Currency;

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
