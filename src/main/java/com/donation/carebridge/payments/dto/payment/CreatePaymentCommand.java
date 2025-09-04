package com.donation.carebridge.payments.dto.payment;

import com.donation.carebridge.payments.domain.payment.Currency;

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
