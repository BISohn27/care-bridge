package com.donation.carebridge.donation.infrastructure.payment.repository;

import com.donation.carebridge.donation.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPaymentRepository extends JpaRepository<Payment, String> {
}
