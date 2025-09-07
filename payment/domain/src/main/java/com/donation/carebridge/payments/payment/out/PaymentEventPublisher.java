package com.donation.carebridge.payments.payment.out;

import com.donation.carebridge.payments.payment.event.PaymentEventPublished;

public interface PaymentEventPublisher {

    void publish(PaymentEventPublished event);
}
