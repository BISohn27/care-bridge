package com.donation.carebridge.donation.domain.donation.application.in;

import java.time.LocalDateTime;

public interface DonationBatchExpirator {

    void expireBatch(LocalDateTime expireThreshold, int batchSize);
}
