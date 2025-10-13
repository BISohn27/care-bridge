package com.donation.carebridge.common.domain.idempotency.application.out;

public interface IdempotencyRepository {

    boolean reserveIdempotencyKey(String context, String idempotencyKey);
    void completeIdempotencyKey(String context, String idempotencyKey);
    void cancelIdempotencyKey(String context, String idempotencyKey);
}
