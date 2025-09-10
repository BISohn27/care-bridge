package com.donation.carebridge.payments.payment.application;

import com.donation.carebridge.payments.payment.event.PaymentEventPublished;
import com.donation.carebridge.payments.payment.model.PaymentEvent;
import com.donation.carebridge.payments.payment.out.PaymentEventPublisher;
import com.donation.carebridge.payments.payment.out.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentEventPublisher eventPublisher;
    private final PaymentEventRepository eventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentEventPublished(PaymentEventPublished event) {
        Optional<PaymentEvent> found = eventRepository.findByEventId(event.eventId());
        if (found.isEmpty()) {
            log.error("EVENT_PROCESSING_FAILED: Payment not found for event processing - paymentId: {}",
                    event.paymentId());
        }
        eventPublisher.publish(event);
        found.get().markAsPublished();
    }
}
