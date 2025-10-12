package com.donation.carebridge.donation.domain.donation.application;

import com.donation.carebridge.donation.domain.donation.application.out.DonationRepository;
import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterCommand;
import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donationcase.application.model.DonationCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
class DonationTransactionService {

    private final DonationRepository donationRepository;

    @Transactional
    Donation saveDonation(DonationRegisterCommand registerCommand, DonationCase donationCase) {
        Donation donation = Donation.create(
                donationCase,
                registerCommand.donorId(),
                registerCommand.amount(),
                registerCommand.currency());
        donation = donationRepository.save(donation);
        return donation;
    }

    @Transactional
    Donation completeDonation(String donationId) {
        Donation donation = getDonationWithCase(donationId);
        donation.complete();
        return donation;
    }

    private Donation getDonationWithCase(String donationId) {
        return donationRepository.findWithCase(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }

    @Transactional
    Donation cancelDonation(String donationId) {
        Donation donation = getDonationWithCase(donationId);
        donation.cancel();
        return donation;
    }

    @Transactional
    List<Donation> expireDonations(LocalDateTime expiredTime) {
        List<Donation> expiredDonations = donationRepository.findExpired(expiredTime);
        if  (!expiredDonations.isEmpty()) {
            expiredDonations.forEach(Donation::expire);
        }
        return expiredDonations;
    }
}
