package com.donation.carebridge.donation.infrastructure.payment.repository;

import com.donation.carebridge.donation.domain.payment.application.out.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisIdempotencyRepository implements IdempotencyRepository {

    private static final String PROCESS = "PROCESS";
    private static final String COMPLETE = "COMPLETE";

    private static final Duration DEFAULT_DURATION = Duration.ofSeconds(3600);

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean reserveIdempotencyKey(String context, String idempotencyKey) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                getKey(context, idempotencyKey),
                PROCESS,
                DEFAULT_DURATION));
    }

    private String getKey(String context, String idempotencyKey) {
        return context + ":" + idempotencyKey;
    }

    @Override
    public void completeIdempotencyKey(String context, String idempotencyKey) {
        redisTemplate.opsForValue().set(getKey(context, idempotencyKey), COMPLETE, DEFAULT_DURATION);
    }

    @Override
    public void cancelIdempotencyKey(String context, String idempotencyKey) {
        redisTemplate.delete(getKey(context, idempotencyKey));
    }
}
