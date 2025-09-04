package com.donation.carebridge.payments.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payment_events")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", length = 36, nullable = false)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", length = 20)
    private Origin origin;

    @Column(name = "event_id", length = 100, nullable = false, unique = true)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 50, nullable = false)
    private PaymentEventType eventType;

    @Column(name = "raw_payload", nullable = false, columnDefinition = "TEXT")
    private String rawPayload;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PaymentEvent(String paymentId, Origin origin, String eventId, PaymentEventType eventType, String rawPayload) {
        this.paymentId = paymentId;
        this.origin = origin;
        this.eventId = eventId;
        this.eventType = eventType;
        this.rawPayload = rawPayload;
    }

    public static PaymentEvent create(String paymentId, String rawPayload) {
        return new PaymentEvent(
                paymentId,
                Origin.SYSTEM,
                EventId.of(paymentId, PaymentEventType.CREATED).getValue(),
                PaymentEventType.CREATED,
                rawPayload
        );
    }
}