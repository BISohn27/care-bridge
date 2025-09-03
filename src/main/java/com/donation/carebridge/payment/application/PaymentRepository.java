package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);
}
