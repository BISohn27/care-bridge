package com.donation.carebridge.payments.payment.usecase;

import com.donation.carebridge.payments.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.payments.payment.dto.CreatePaymentResult;

public interface CreatePaymentUseCase {

    CreatePaymentResult create(CreatePaymentRequest createRequest);
}
