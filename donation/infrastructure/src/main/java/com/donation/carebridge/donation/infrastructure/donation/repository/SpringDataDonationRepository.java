package com.donation.carebridge.donation.infrastructure.donation.repository;

import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataDonationRepository extends JpaRepository<Donation, String> {

    @Query("SELECT d FROM Donation d JOIN FETCH DonationCase dc WHERE d.id = :donationId")
    Optional<Donation> findByIdWithDonationCase(String donationId);

    Optional<Donation> findByDonationCaseIdAndDonorIdAndStatus(String donationCaseId, String donorId, DonationStatus donationStatus);

    List<Donation> findAllByCreatedAtLessThanAndStatus(LocalDateTime createdAt, DonationStatus status);
}
