package com.donation.carebridge.donation.domain.donation.application.in;

import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterCommand;
import com.donation.carebridge.donation.domain.donation.dto.DonationRegisterResult;

public interface DonationRegister {

    DonationRegisterResult register(DonationRegisterCommand registerRequest);
}
