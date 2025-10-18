package com.donation.carebridge.donation.domain.donation.application;

import com.donation.carebridge.common.domain.common.application.out.TransactionExecutor;
import com.donation.carebridge.common.domain.idempotency.annotation.IdempotencyCheck;
import com.donation.carebridge.donation.domain.donation.application.in.DonationCanceller;
import com.donation.carebridge.donation.domain.donation.application.in.DonationCompleter;
import com.donation.carebridge.donation.domain.donation.application.in.DonationExpirator;
import com.donation.carebridge.donation.domain.donation.application.in.DonationRegister;
import com.donation.carebridge.donation.domain.donation.application.out.DonationQuotaManager;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationService implements DonationRegister, DonationCompleter, DonationCanceller, DonationExpirator {

    private final TransactionExecutor transactionExecutor;
    private final DonationRepository donationRepository;
    private final DonationCaseFinder donationCaseFinder;
    private final DonationQuotaManager quotaManager;
    private final PaymentInitiator paymentInitiator;

    @Override
    @IdempotencyCheck(prefix = "donation-register")
    public DonationRegisterResult register(DonationRegisterCommand registerCommand) {
        DonationCase donationCase = donationCaseFinder.find(registerCommand.donationCaseId());

        checkCaseFundable(donationCase);
        checkDuplicate(registerCommand);

        Donation donation = processDonation(registerCommand, donationCase);
        return initiatePayment(registerCommand, donation);
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

    private Donation processDonation(DonationRegisterCommand registerCommand, DonationCase donationCase) {
        try {
            quotaManager.reserve(registerCommand.donationCaseId(), registerCommand.amount());
            return transactionExecutor.executeInTransaction(() -> {
                Donation donation = Donation.create(
                        donationCase,
                        registerCommand.donorId(),
                        registerCommand.amount(),
                        registerCommand.currency());
                donation = donationRepository.save(donation);
                return donation;
            });
        } catch (Exception e) {
            quotaManager.release(registerCommand.donationCaseId(), registerCommand.amount());
            throw e;
        }
    }

    private DonationRegisterResult initiatePayment(DonationRegisterCommand registerCommand, Donation donation) {
        CreatePaymentResult paymentResult = paymentInitiator.initiate(new CreatePaymentRequest(
                donation.getId(),
                registerCommand.currency(),
                registerCommand.amount(),
                registerCommand.pgProviderCode(),
                registerCommand.paymentMethod(),
                registerCommand.idempotencyKey()));

        return new DonationRegisterResult(
                donation.getId(),
                donation.getCurrency(),
                donation.getAmount(),
                paymentResult
        );
    }

    @Override
    public void complete(String donationId) {
        Donation completed = transactionExecutor.executeInTransaction(() -> {
            Donation donation = getDonationWithCase(donationId);
            donation.complete();
            return donation;
        });
        quotaManager.confirm(completed.getDonationCase().getId(), completed.getAmount());
    }

    private Donation getDonationWithCase(String donationId) {
        return donationRepository.findWithCase(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
    }

    @Override
    public void cancel(String donationId) {
        Donation cancelled = transactionExecutor.executeInTransaction(() -> {
            Donation donation = getDonationWithCase(donationId);
            donation.cancel();
            return donation;
        });
        quotaManager.release(cancelled.getDonationCase().getId(), cancelled.getAmount());
    }

    @Override
    public void expire(String donationId) {
        Donation donation = getDonationWithCase(donationId);
        transactionExecutor.executeInTransaction(donation::expire);
        quotaManager.release(donation.getDonationCase().getId(), donation.getAmount());
    }

    @Override
    public void expireAll(List<String> donationIds) {
        if (donationIds.isEmpty()) {
            return;
        }

        List<Donation> expiredDonations = transactionExecutor.executeInTransaction(() -> {
            List<Donation> donations = donationRepository.findAll(donationIds);
            donations.forEach(Donation::expire);
            return donations;
        });
        releaseReserved(expiredDonations);
    }

    private void releaseReserved(List<Donation> expiredDonations) {
        Map<String, Long> reserved = expiredDonations.stream()
                .collect(Collectors.groupingBy(
                        donation -> donation.getDonationCase().getId(),
                        Collectors.summingLong(Donation::getAmount)
                ));
        quotaManager.releaseMultiple(reserved);
    }
}
