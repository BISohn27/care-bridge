package com.donation.carebridge.donation.batch;

import com.donation.carebridge.donation.domain.donation.application.in.DonationBatchExpirator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DonationExpirationScheduler {

    @Value("${care-bridge.donation.expire.timeout-seconds:3600}")
    private long expiredSeconds;

    @Value("${care-bridge.donation.expire.batch-size:1000}")
    private int batchSize;

    private final DonationBatchExpirator batchExpirator;

    @Scheduled(fixedDelayString = "${care-bridge.donation.expiration-check-interval.ms:10000}")
    public void scheduledTask() {
        LocalDateTime expireThreshold = LocalDateTime.now().plusSeconds(expiredSeconds);
        batchExpirator.expireBatch(expireThreshold, batchSize);
    }
}
