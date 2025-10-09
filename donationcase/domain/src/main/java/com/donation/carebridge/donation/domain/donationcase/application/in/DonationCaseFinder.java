package com.donation.carebridge.donation.domain.donationcase.application.in;

import com.donation.carebridge.donation.domain.donationcase.application.model.DonationCase;

public interface DonationCaseFinder {

    DonationCase find(String donationCaseId);
}
