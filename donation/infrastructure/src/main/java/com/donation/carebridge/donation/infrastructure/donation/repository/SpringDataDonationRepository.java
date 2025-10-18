package com.donation.carebridge.donation.infrastructure.donation.repository;

import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataDonationRepository extends JpaRepository<Donation, String> {

    @Query("SELECT d FROM Donation d JOIN FETCH DonationCase dc WHERE d.id = :donationId")
    Optional<Donation> findByIdWithDonationCase(String donationId);

    Optional<Donation> findByDonationCaseIdAndDonorIdAndStatus(String donationCaseId, String donorId, DonationStatus donationStatus);

    @Query("""
        SELECT d FROM Donation d
        JOIN FETCH d.donationCase
        WHERE d.status = 'PENDING'
            AND d.createdAt < :threshold
        ORDER BY d.createdAt ASC, d.id ASC
        LIMIT :limit
    """)
    List<Donation> findExpiredFirst(LocalDateTime threshold, int limit);

    @Query("""
        SELECT d FROM Donation d
        JOIN FETCH d.donationCase
        WHERE d.status = 'PENDING'
            AND d.createdAt < :threshold
            AND (
              d.createdAt > :cursorTime
              OR (d.createdAt = :cursorTime AND d.id > :cursorId)
            )
        ORDER BY d.createdAt ASC, d.id ASC
        LIMIT :limit
    """)
    List<Donation> findExpiredWithCursor(
            LocalDateTime threshold,
            LocalDateTime cursorTime,
            String cursorId,
            int limit
    );

    @Modifying
    @Query("""
        UPDATE Donation d
        SET d.status = 'EXPIRED'
        WHERE d.id IN :donationIds
            AND d.status = 'PENDING'
    """)
    int updateExpired(List<String> donationIds);
}
