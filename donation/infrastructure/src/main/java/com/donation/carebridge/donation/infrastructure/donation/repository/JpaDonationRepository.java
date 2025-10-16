package com.donation.carebridge.donation.infrastructure.donation.repository;

import com.donation.carebridge.donation.domain.donation.application.out.DonationRepository;
import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaDonationRepository implements DonationRepository {

    private final EntityManager entityManager;
    private final SpringDataDonationRepository dataRepository;

    @Override
    public Donation save(Donation donation) {
        return dataRepository.save(donation);
    }

    @Override
    public Optional<Donation> find(String donationId) {
        return dataRepository.findById(donationId);
    }

    @Override
    public Optional<Donation> findWithCase(String donationId) {
        return dataRepository.findByIdWithDonationCase(donationId);
    }

    @Override
    public Optional<Donation> find(String caseId, String donorId, DonationStatus status) {
        return dataRepository.findByDonationCaseIdAndDonorIdAndStatus(caseId, donorId, status);
    }

    @Override
    public List<Donation> findExpired(LocalDateTime expiredDateTime, int batchSize, String nextCursor, LocalDateTime cursorTime) {
        if (nextCursor == null && cursorTime == null) {
            return dataRepository.findExpiredFirst(expiredDateTime, batchSize);
        }
        return dataRepository.findExpiredWithCursor(expiredDateTime, cursorTime, nextCursor, batchSize);
    }

    @Override
    public List<Donation> expireAndRefresh(List<String> donationIds) {
        dataRepository.updateExpired(donationIds);
        entityManager.clear();
        return dataRepository.findAllById(donationIds);
    }
}
