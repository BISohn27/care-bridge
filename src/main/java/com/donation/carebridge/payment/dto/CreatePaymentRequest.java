package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payment.domain.Currency;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;

public record CreatePaymentRequest(
    String caseId,
    String donorId,
    Currency currency,
    long amount,
    PgProviderCode pgProviderCode,
    PaymentMethod paymentMethod,
    String idempotencyKey) {
}
