package com.donation.carebridge.donation.domain.donation.application.in;

import java.util.List;

public interface DonationExpirator {

    void expire(String donationId);
    void expireAll(List<String> donationIds);
}
