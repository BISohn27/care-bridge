package com.donation.carebridge.donation.infrastructure.payment.publisher;

import com.donation.carebridge.donation.domain.payment.event.PaymentEventPublished;
import com.donation.carebridge.donation.domain.payment.out.PaymentEventPublisher;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Fallback
@Component
public class DummyPublisher implements PaymentEventPublisher {

    @Override
    public void publish(PaymentEventPublished event) {

    }
}
