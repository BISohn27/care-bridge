package com.donation.carebridge.payments.infra.payment;

import com.donation.carebridge.payments.domain.payment.Payment;
import com.donation.carebridge.payments.domain.payment.PaymentRepository;
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
