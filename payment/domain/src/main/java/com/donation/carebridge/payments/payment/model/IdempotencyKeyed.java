package com.donation.carebridge.payments.payment.model;

public interface IdempotencyKeyed {

    String idempotencyKey();
}
