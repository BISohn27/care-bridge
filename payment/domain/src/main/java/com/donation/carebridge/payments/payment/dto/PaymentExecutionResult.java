package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.payment.model.PaymentStatus;

public record PaymentExecutionResult(
        NextAction nextAction,
        String pgPaymentId,
        String rawPayload,
        PaymentStatus status,
        String reasonCode,
        String reasonMessage) {

    public PaymentExecutionResult(NextAction nextAction) {
        this(nextAction, null, null, null, null, null);
    }

    public PaymentExecutionResult(NextAction nextAction, String rawPayload) {
        this(nextAction, null, rawPayload, null, null, null);
    }

    public PaymentExecutionResult(String reasonCode, String reasonMessage) {
        this(null, null, null, PaymentStatus.FAILED, reasonCode, reasonMessage);
    }

    public PaymentExecutionResult(PaymentStatus paymentStatus, String pgPaymentId, String rawPayload) {
        this(null, pgPaymentId, rawPayload, paymentStatus, null, null);
    }
}
