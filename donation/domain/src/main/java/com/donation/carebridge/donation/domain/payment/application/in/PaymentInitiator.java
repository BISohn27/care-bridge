package com.donation.carebridge.donation.domain.payment.application.in;

import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;

public interface PaymentInitiator {

    CreatePaymentResult initiate(CreatePaymentRequest createRequest);
}
