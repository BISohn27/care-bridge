package com.donation.carebridge.donation.domain.donation.application.out;

import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DonationRepository {

    Donation save(Donation donation);
    Optional<Donation> find(String donationId);
    Optional<Donation> findWithCase(String donationId);
    Optional<Donation> find(String caseId, String donorId, DonationStatus status);
    List<Donation> findAll(List<String> donationIds);
    List<Donation> findExpired(LocalDateTime expireThreshold, int batchSize, @Nullable String nextCursor, @Nullable LocalDateTime cursorTime);
}
