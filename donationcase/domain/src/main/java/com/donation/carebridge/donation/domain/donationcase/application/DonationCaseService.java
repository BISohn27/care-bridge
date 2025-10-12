package com.donation.carebridge.donation.domain.donationcase.application;

import com.donation.carebridge.donation.domain.donationcase.application.in.DonationCaseFinder;
import com.donation.carebridge.donation.domain.donationcase.application.model.DonationCase;
import org.springframework.stereotype.Service;

@Service
public class DonationCaseService implements DonationCaseFinder {

    @Override
    public DonationCase find(String donationCaseId) {
        return null;
    }
}
