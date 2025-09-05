package com.donation.carebridge.payments.payment.dto;

public record PaymentExecutionResult(NextAction nextAction, String rawPayload) {
}
