package com.donation.carebridge.donation.domain.donation.dto;

import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;
import com.donation.carebridge.donation.domain.payment.model.Currency;

public record DonationRegisterResult(String donationId, Currency currency, long amount, CreatePaymentResult payment) {
}
