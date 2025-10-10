package com.donation.carebridge.donation.domain.payment.event;

import com.donation.carebridge.donation.domain.payment.model.Origin;
import com.donation.carebridge.donation.domain.payment.model.PaymentEvent;
import com.donation.carebridge.donation.domain.payment.model.PaymentEventType;

import java.util.Objects;

public record PaymentEventPublished(
    String donationId,
    String paymentId,
    Origin origin,
    String eventId,
    PaymentEventType eventType,
    String rawPayload
) {
    
    public PaymentEventPublished {
        Objects.requireNonNull(paymentId, "paymentId cannot be null");
        Objects.requireNonNull(origin, "origin cannot be null");
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(eventType, "eventType cannot be null");
    }
    
    public static PaymentEventPublished from(String donationId, PaymentEvent paymentEvent) {
        return new PaymentEventPublished(
            donationId,
            paymentEvent.getPaymentId(),
            paymentEvent.getOrigin(),
            paymentEvent.getEventId(),
            paymentEvent.getEventType(),
            paymentEvent.getRawPayload()
        );
    }
}