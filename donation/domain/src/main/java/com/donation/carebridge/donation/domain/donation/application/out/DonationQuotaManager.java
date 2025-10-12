package com.donation.carebridge.donation.domain.donation.application.out;

public interface DonationQuotaManager {

    void reserve(String donationCaseId, long amount);
    void confirm(String donationCaseId, long amount);
    void release(String donationCaseId, long amount);
}
