package com.donation.carebridge.payments.dto.payment;

import com.donation.carebridge.payments.domain.payment.Currency;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;

public record CreatePaymentRequest(
    String caseId,
    String donorId,
    Currency currency,
    long amount,
    PgProviderCode pgProviderCode,
    PaymentMethod paymentMethod,
    String idempotencyKey) {
}
