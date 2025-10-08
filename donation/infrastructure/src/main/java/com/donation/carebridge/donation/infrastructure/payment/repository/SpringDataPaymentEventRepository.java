package com.donation.carebridge.donation.infrastructure.payment.repository;

import com.donation.carebridge.donation.domain.payment.model.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPaymentEventRepository extends JpaRepository<PaymentEvent, Long> {

    Optional<PaymentEvent> findByEventId(String eventId);
}
