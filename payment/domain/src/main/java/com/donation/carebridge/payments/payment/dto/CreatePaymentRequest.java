package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.payment.model.Currency;
import com.donation.carebridge.payments.pg.model.PgProviderCode;

public record CreatePaymentRequest(
    String caseId,
    String donorId,
    Currency currency,
    long amount,
    PgProviderCode pgProviderCode,
    PaymentMethod paymentMethod,
    String idempotencyKey) {
}
