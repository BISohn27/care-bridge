package com.donation.carebridge.donation.domain.donation.application;

import com.donation.carebridge.donation.domain.donation.application.in.DonationCanceller;
import com.donation.carebridge.donation.domain.donation.application.in.DonationCompleter;
import com.donation.carebridge.donation.domain.donation.application.in.DonationExpirator;
import com.donation.carebridge.donation.domain.donation.application.in.DonationRegister;
import com.donation.carebridge.donation.domain.donation.application.out.DonationRepository;
import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterCommand;
import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterResult;
import com.donation.carebridge.donation.domain.donation.model.Donation;
import com.donation.carebridge.donation.domain.donation.model.DonationStatus;
import com.donation.carebridge.donation.domain.donationcase.application.in.DonationCaseFinder;
import com.donation.carebridge.donation.domain.donationcase.application.model.DonationCase;
import com.donation.carebridge.donation.domain.payment.application.in.PaymentInitiator;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationService implements DonationRegister, DonationCompleter, DonationCanceller, DonationExpirator {

    @Value("${care-bridge.donation.timeout-seconds:3600}")
    private long expiredSeconds;

    private final DonationRepository donationRepository;
    private final DonationCaseFinder donationCaseFinder;
    private final PaymentInitiator paymentInitiator;

    @Override
    @Transactional
    public DonationRegisterResult register(DonationRegisterCommand registerCommand) {
        DonationCase donationCase = donationCaseFinder.find(registerCommand.donationCaseId());

        checkCaseFundable(donationCase);
        checkDuplicate(registerCommand);

        Donation donation = Donation.create(
                donationCase,
                registerCommand.donorId(),
                registerCommand.amount(),
                registerCommand.currency());
        donation = donationRepository.save(donation);

        CreatePaymentResult createPaymentResult = paymentInitiator.initiate(new CreatePaymentRequest(
                donation.getId(),
                registerCommand.currency(),
                registerCommand.amount(),
                registerCommand.pgProviderCode(),
                registerCommand.paymentMethod(),
                registerCommand.idempotencyKey()));

        return new DonationRegisterResult(donation.getId(), donation.getCurrency(), donation.getAmount(), createPaymentResult);
    }

    private void checkCaseFundable(DonationCase donationCase) {
        if (!donationCase.isFundable()) {
            throw new IllegalStateException("DonationCase is not fundable");
        }
    }

    private void checkDuplicate(DonationRegisterCommand createRequest) {
        Optional<Donation> found =
                donationRepository.find(createRequest.donationCaseId(), createRequest.donorId(), DonationStatus.PENDING);

        if (found.isPresent()) {
            throw new IllegalStateException("Donation already exists");
        }
    }

    @Override
    @Transactional
    public void complete(String donationId) {
        Donation donation = getDonationWithCase(donationId);
        donation.complete();
    }

    private Donation getDonationWithCase(String donationId) {
        return donationRepository.findWithCase(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }

    @Override
    @Transactional
    public void cancel(String donationId) {
        Donation donation = getDonationWithCase(donationId);
        donation.cancel();
    }

    @Override
    @Transactional
    public void expire() {
        List<Donation> expiredDonations = donationRepository.findExpired(LocalDateTime.now().minusSeconds(expiredSeconds));

        if  (!expiredDonations.isEmpty()) {
            expiredDonations.forEach(Donation::expire);
        }
    }
}
