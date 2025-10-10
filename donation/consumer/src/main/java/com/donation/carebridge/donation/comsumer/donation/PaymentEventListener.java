package com.donation.carebridge.donation.comsumer.donation;

import com.donation.carebridge.donation.domain.donation.application.in.DonationCompleter;
import com.donation.carebridge.donation.domain.payment.event.PaymentEventPublished;
import com.donation.carebridge.donation.domain.payment.model.PaymentEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final DonationCompleter donationCompleter;

    @EventListener
    public void complete(PaymentEventPublished event) {
        if (event.eventType() == PaymentEventType.CONFIRMED) {
            donationCompleter.complete(event.donationId());
        }
    }
}
