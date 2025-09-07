package com.donation.carebridge.payments.payment.out;

import com.donation.carebridge.payments.payment.model.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);
}
