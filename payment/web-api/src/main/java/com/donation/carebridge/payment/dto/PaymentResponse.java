package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payments.payment.dto.CreatePaymentResult;
import com.donation.carebridge.payments.payment.dto.NextAction;
import com.donation.carebridge.payments.payment.model.PaymentStatus;
import com.donation.carebridge.payments.pg.model.PgFlowType;
import com.donation.carebridge.payments.pg.model.PgProviderCode;

public record PaymentResponse(
        String paymentId,
        PaymentStatus status,
        PgFlowType nextActionFlowType,
        PgProviderCode nextActionProvider,
        Object nextActionPayload
) {
    public static PaymentResponse from(CreatePaymentResult result) {
        NextAction nextAction = result.nextAction();
        return new PaymentResponse(
                result.paymentId(),
                result.status(),
                nextAction.flowType(),
                nextAction.provider(),
                nextAction.payload()
        );
    }
}
