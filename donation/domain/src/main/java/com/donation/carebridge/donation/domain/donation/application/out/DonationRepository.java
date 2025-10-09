package com.donation.carebridge.donation.domain.donation.application.out;

import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;

import java.util.Optional;

public interface DonationRepository {

    Donation save(Donation donation);
    Optional<Donation> find(String donationId);
    Optional<Donation> find(String caseId, String donorId, DonationStatus status);
}
