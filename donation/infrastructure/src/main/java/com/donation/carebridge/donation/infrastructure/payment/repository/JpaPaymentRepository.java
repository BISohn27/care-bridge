package com.donation.carebridge.donation.infrastructure.payment.repository;

import com.donation.carebridge.donation.domain.payment.model.Payment;
import com.donation.carebridge.donation.domain.payment.application.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPaymentRepository implements PaymentRepository {

    private final SpringDataPaymentRepository dataRepository;

    @Override
    public Optional<Payment> findById(String id) {
        return dataRepository.findById(id);
    }

    @Override
    public Payment save(Payment payment) {
        return dataRepository.save(payment);
    }

}
