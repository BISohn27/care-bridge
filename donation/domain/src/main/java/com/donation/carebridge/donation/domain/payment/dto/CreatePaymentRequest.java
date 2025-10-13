package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.common.domain.idempotency.model.IdempotencyKeyed;
import com.donation.carebridge.donation.domain.payment.model.Currency;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

public record CreatePaymentRequest(
    String donationId,
    Currency currency,
    long amount,
    PgProviderCode pgProviderCode,
    PaymentMethod paymentMethod,
    String idempotencyKey) implements IdempotencyKeyed {
}
