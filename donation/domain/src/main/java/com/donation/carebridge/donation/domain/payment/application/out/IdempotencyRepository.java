package com.donation.carebridge.donation.domain.payment.application.out;

public interface IdempotencyRepository {

    boolean reserveIdempotencyKey(String context, String idempotencyKey);
    void completeIdempotencyKey(String context, String idempotencyKey);
    void cancelIdempotencyKey(String context, String idempotencyKey);
}
