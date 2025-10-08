package com.donation.carebridge.donation.domain.payment.out;

import com.donation.carebridge.donation.domain.payment.event.PaymentEventPublished;

public interface PaymentEventPublisher {

    void publish(PaymentEventPublished event);
}
