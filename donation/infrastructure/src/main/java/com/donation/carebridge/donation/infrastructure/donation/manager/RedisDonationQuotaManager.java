package com.donation.carebridge.donation.infrastructure.donation.manager;

import com.donation.carebridge.donation.domain.donation.application.out.DonationQuotaManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisDonationQuotaManager implements DonationQuotaManager {

    private static final String QUOTA_KEY_PREFIX = "donation:quota:";
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void reserve(String donationCaseId, long amount) {
        String quotaKey = QUOTA_KEY_PREFIX + donationCaseId;

        String luaScript = """
            local targetAmount = tonumber(redis.call('HGET', KEYS[1], 'target'))
            local currentAmount = tonumber(redis.call('HGET', KEYS[1], 'current'))
            local reservedAmount = tonumber(redis.call('HGET', KEYS[1], 'reserved'))
            local requestAmount = tonumber(ARGV[1])

            -- nil 처리
            if targetAmount == nil then targetAmount = 0 end
            if currentAmount == nil then currentAmount = 0 end
            if reservedAmount == nil then reservedAmount = 0 end

            -- 사용 가능 금액 계산
            local available = targetAmount - currentAmount - reservedAmount

            if available < requestAmount then
                return -1  -- 실패: 목표액 초과
            end

            -- 예약 금액 증가
            redis.call('HINCRBY', KEYS[1], 'reserved', requestAmount)
            return 1  -- 성공
            """;

        Long result = redisTemplate.execute(
            new DefaultRedisScript<>(luaScript, Long.class),
            Collections.singletonList(quotaKey),
            String.valueOf(amount)
        );

        if (result == null || result == -1) {
            throw new IllegalStateException("모금 목표액을 초과했습니다.");
        }
    }

    @Override
    public void confirm(String donationCaseId, long amount) {
        String quotaKey = QUOTA_KEY_PREFIX + donationCaseId;

        String luaScript = """
            redis.call('HINCRBY', KEYS[1], 'reserved', -tonumber(ARGV[1]))
            redis.call('HINCRBY', KEYS[1], 'current', tonumber(ARGV[1]))
            return 1
            """;

        redisTemplate.execute(
            new DefaultRedisScript<>(luaScript, Long.class),
            Collections.singletonList(quotaKey),
            String.valueOf(amount)
        );
    }

    @Override
    public void release(String donationCaseId, long amount) {
        String quotaKey = QUOTA_KEY_PREFIX + donationCaseId;

        redisTemplate.opsForHash().increment(quotaKey, "reserved", -amount);
    }

    @Override
    public void releaseMultiple(Map<String, Long> releaseMap) {
        if (releaseMap.isEmpty()) {
            return;
        }

        String luaScript = """
            for i = 1, #KEYS do
                local quotaKey = KEYS[i]
                local amount = tonumber(ARGV[i])
                redis.call('HINCRBY', quotaKey, 'reserved', -amount)
            end
            return 1
            """;

        List<String> keys = releaseMap.keySet().stream()
            .map(caseId -> QUOTA_KEY_PREFIX + caseId)
            .toList();

        String[] amounts = releaseMap.values().stream()
            .map(String::valueOf)
            .toArray(String[]::new);

        redisTemplate.execute(
            new DefaultRedisScript<>(luaScript, Long.class),
            keys,
            amounts
        );
    }
}