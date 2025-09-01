package com.donation.carebridge.payment.application;

import java.util.Optional;

public interface IdempotencyStore<T> {

    Optional<T> findByKey(String key);
    void save(String key, T payment);
}
