package com.donation.carebridge.donation.domain.donationcase.application.model;

import com.donation.carebridge.common.domain.UUIDBaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Entity
public class DonationCase extends UUIDBaseTimeEntity {

    @Column(name = "title", length = 250, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private DonationCaseStatus status;

    @Column(name = "beneficiary_id", length = 36, nullable = false)
    private String beneficiaryId;

    @Column(name = "target_amount", nullable = false)
    private long targetAmount;

    @Column(name = "current_amount", nullable = false)
    private long currentAmount = 0;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public boolean isFundable() {
        return this.status == DonationCaseStatus.FUNDING;
    }

    public void addAmount(long amount) {
        if (currentAmount + amount > targetAmount) {
            throw new IllegalStateException("Current amount is greater than target amount");
        }
        this.currentAmount += amount;
    }
}
