package com.donation.carebridge.payment.infra;

import com.donation.carebridge.payment.application.CreatePaymentResult;
import com.donation.carebridge.payment.application.IdempotencyStore;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryIdempotencyStore implements IdempotencyStore<CreatePaymentResult> {

    private final Map<String, CreatePaymentResult> createPaymentResults = new ConcurrentHashMap<>();

    @Override
    public Optional<CreatePaymentResult> findByKey(String key) {
        return Optional.ofNullable(createPaymentResults.get(createKey(key)));
    }

    public static String createKey(String idempotencyKey) {
        return String.format("payment:idem:create:%s", idempotencyKey);
    }

    @Override
    public void save(String key, CreatePaymentResult payment) {
        createPaymentResults.put(createKey(key), payment);
    }
}
