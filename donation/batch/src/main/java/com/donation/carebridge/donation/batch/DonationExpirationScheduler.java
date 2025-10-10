package com.donation.carebridge.donation.batch;

import com.donation.carebridge.donation.domain.donation.application.in.DonationExpirator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonationExpirationScheduler {

    private final DonationExpirator expirator;

    @Scheduled(fixedDelayString = "${care-bridge.donation.expiration-check-interval.ms:10000}")
    public void scheduledTask() {
        expirator.expire();
    }
}
