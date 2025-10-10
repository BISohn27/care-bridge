package com.donation.carebridge.donation.producer.payment;

import com.donation.carebridge.donation.domain.payment.event.PaymentEventPublished;
import com.donation.carebridge.donation.domain.payment.application.out.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Fallback
@Component
@RequiredArgsConstructor
public class DummyPublisher implements PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(PaymentEventPublished event) {
        applicationEventPublisher.publishEvent(event);
    }
}
