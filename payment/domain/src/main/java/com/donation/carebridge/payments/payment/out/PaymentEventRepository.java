package com.donation.carebridge.payments.payment.out;

import com.donation.carebridge.payments.payment.model.PaymentEvent;

import java.util.Optional;

public interface PaymentEventRepository {

    void save(PaymentEvent paymentEvent);
    Optional<PaymentEvent> findById(String id);
}
