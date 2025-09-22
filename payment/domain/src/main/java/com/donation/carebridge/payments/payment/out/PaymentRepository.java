package com.donation.carebridge.payments.payment.out;

import com.donation.carebridge.payments.payment.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);
    Optional<Payment> findById(String id);
}
