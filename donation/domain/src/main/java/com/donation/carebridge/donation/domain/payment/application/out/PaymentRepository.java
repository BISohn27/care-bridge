package com.donation.carebridge.donation.domain.payment.application.out;

import com.donation.carebridge.donation.domain.payment.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);
    Optional<Payment> findById(String id);
}
