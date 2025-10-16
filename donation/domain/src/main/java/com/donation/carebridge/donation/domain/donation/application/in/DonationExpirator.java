package com.donation.carebridge.donation.domain.donation.application.in;

import java.time.LocalDateTime;

public interface DonationExpirator {

    void expire(LocalDateTime expireThreshold, int batchSize);
}
