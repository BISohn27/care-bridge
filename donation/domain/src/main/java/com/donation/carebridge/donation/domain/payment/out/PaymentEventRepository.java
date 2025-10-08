package com.donation.carebridge.donation.domain.payment.out;

import com.donation.carebridge.donation.domain.payment.model.PaymentEvent;

import java.util.Optional;

public interface PaymentEventRepository {

    void save(PaymentEvent paymentEvent);
    Optional<PaymentEvent> findByEventId(String eventId);
}
