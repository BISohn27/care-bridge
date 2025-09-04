package com.donation.carebridge.payments.infra.payment;


import com.donation.carebridge.payments.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPaymentRepository extends JpaRepository<Payment, String> {
}
