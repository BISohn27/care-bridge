package com.donation.carebridge.donation.domain.donation.application;

import com.donation.carebridge.donation.domain.donation.application.in.DonationBatchExpirator;
import com.donation.carebridge.donation.domain.donation.application.in.DonationExpirator;
import com.donation.carebridge.donation.domain.donation.application.out.DonationRepository;
import com.donation.carebridge.donation.domain.donation.model.Donation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationBatchService implements DonationBatchExpirator {

    private final DonationExpirator donationExpirator;
    private final DonationRepository donationRepository;

    @Override
    public void expireBatch(LocalDateTime expireThreshold, int batchSize) {
        String lastProcessedId = null;
        LocalDateTime lastProcessedTime = null;

        while (true) {
            List<Donation> expiredDonations = donationRepository.findExpired(
                    expireThreshold, batchSize, lastProcessedId, lastProcessedTime);

            if (expiredDonations.isEmpty()) {
                break;
            }

            donationExpirator.expireAll(expiredDonations.stream().map(Donation::getId).toList());

            int currentSize = expiredDonations.size();
            if (currentSize < batchSize) {
                break;
            }
            Donation lastProcessed = expiredDonations.get(currentSize - 1);
            lastProcessedId = lastProcessed.getId();
            lastProcessedTime = lastProcessed.getCreatedAt();
        }
    }
}
