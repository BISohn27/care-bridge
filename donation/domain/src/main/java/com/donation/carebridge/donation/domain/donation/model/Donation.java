package com.donation.carebridge.donation.domain.donation.model;

import com.donation.carebridge.common.domain.UUIDBaseTimeEntity;
import com.donation.carebridge.donation.domain.donationcase.application.model.DonationCase;
import com.donation.carebridge.donation.domain.payment.model.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Donation extends UUIDBaseTimeEntity {

    private String donorId;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;

    private long amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_case_id", referencedColumnName = "id")
    private DonationCase donationCase;

    private LocalDateTime completedAt;

    public static Donation create(DonationCase donationCase, String donorId, long amount, Currency currency) {
        Donation donation = new Donation();
        donation.donorId = donorId;
        donation.status = DonationStatus.PENDING;
        donation.amount = amount;
        donation.currency = currency;
        donation.donationCase = donationCase;

        return donation;
    }

    public void complete() {
        checkModifiable();

        this.donationCase.addAmount(amount);
        this.status = DonationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    private void checkModifiable() {
        if (status != DonationStatus.PENDING) {
            throw new IllegalStateException("Donation is not pending");
        }
    }

    public void cancel() {
        checkModifiable();
        this.status = DonationStatus.CANCELLED;
    }

    public void expire() {
        checkModifiable();
        this.status = DonationStatus.EXPIRED;
    }
}
