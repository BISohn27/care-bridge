package com.donation.carebridge.common.domain.idempotency.model;

public interface IdempotencyKeyed {

    String idempotencyKey();
}
