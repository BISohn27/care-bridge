package com.donation.carebridge.donation.domain.donation.dto;

import com.donation.carebridge.donation.domain.payment.dto.PaymentMethod;
import com.donation.carebridge.donation.domain.payment.model.Currency;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

public record DonationRegisterCommand(
        String donationCaseId,
        String donorId,
        Currency currency,
        long amount,
        PgProviderCode pgProviderCode,
        PaymentMethod paymentMethod,
        String idempotencyKey) {

    private static final long MIN_DONATION_AMOUNT = 1000;

    public DonationRegisterCommand {
        if (amount < MIN_DONATION_AMOUNT) {
            throw new IllegalArgumentException("Amount must be greater than " + MIN_DONATION_AMOUNT);
        }
    }

    public DonationRegisterCommand(String donationCaseId, String donorId, long amount, String idempotencyKey) {
        this(donationCaseId, donorId, Currency.KRW, amount, PgProviderCode.TOSS, PaymentMethod.CARD, idempotencyKey);
    }
}
