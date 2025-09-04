package com.donation.carebridge.payments.dto.payment;

public record PaymentExecutionResult(NextAction nextAction, String rawPayload) {
}
