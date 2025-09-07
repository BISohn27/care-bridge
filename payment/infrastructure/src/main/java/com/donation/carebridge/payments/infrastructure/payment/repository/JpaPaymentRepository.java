package com.donation.carebridge.payments.infrastructure.payment.repository;

import com.donation.carebridge.payments.payment.model.Payment;
import com.donation.carebridge.payments.payment.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPaymentRepository implements PaymentRepository {

    private final SpringDataPaymentRepository dataRepository;

    @Override
    public Payment save(Payment payment) {
        return dataRepository.save(payment);
    }
}
