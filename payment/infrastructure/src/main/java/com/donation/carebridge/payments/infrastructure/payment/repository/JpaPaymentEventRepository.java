package com.donation.carebridge.payments.infrastructure.payment.repository;

import com.donation.carebridge.payments.payment.model.PaymentEvent;
import com.donation.carebridge.payments.payment.out.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPaymentEventRepository implements PaymentEventRepository {

    private final SpringDataPaymentEventRepository dataRepository;

    @Override
    public void save(PaymentEvent paymentEvent) {
        dataRepository.save(paymentEvent);
    }

    @Override
    public Optional<PaymentEvent> findByEventId(String eventId) {
        return dataRepository.findByEventId(eventId);
    }
}
