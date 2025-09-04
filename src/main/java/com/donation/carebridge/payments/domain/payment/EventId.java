package com.donation.carebridge.payments.domain.payment;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Getter
public class EventId {

    private final String value;

    private EventId(String value) {
        this.value = value;
    }

    public static EventId of(String paymentId, PaymentEventType eventType) {
        String input = paymentId + "|" + eventType;
        UUID uuid = UUID.nameUUIDFromBytes(input.getBytes(StandardCharsets.UTF_8));
        return new EventId(uuid.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventId eventId = (EventId) o;
        return Objects.equals(value, eventId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
