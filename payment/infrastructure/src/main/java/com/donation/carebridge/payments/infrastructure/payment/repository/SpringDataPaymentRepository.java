package com.donation.carebridge.payments.infrastructure.payment.repository;

import com.donation.carebridge.payments.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPaymentRepository extends JpaRepository<Payment, String> {
}
