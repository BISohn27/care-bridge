package com.donation.carebridge.donation.domain.payment.usecase;

import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;

public interface CreatePaymentUseCase {

    CreatePaymentResult create(CreatePaymentRequest createRequest);
}
