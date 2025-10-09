package com.donation.carebridge.donation.domain.donationcase.application.model;

import com.donation.carebridge.common.domain.UUIDBaseTimeEntity;
import jakarta.persistence.Entity;

@Entity
public class DonationCase extends UUIDBaseTimeEntity {

    public boolean isFundable() {
        return true;
    }
}
