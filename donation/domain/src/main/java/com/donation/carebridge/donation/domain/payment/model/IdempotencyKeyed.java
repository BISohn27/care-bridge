package com.donation.carebridge.donation.domain.payment.model;

public interface IdempotencyKeyed {

    String idempotencyKey();
}
