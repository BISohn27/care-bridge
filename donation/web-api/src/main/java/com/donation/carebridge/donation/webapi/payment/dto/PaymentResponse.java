package com.donation.carebridge.donation.webapi.payment.dto;

import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentResult;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;
import com.donation.carebridge.donation.domain.payment.dto.NextAction;
import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;
import com.donation.carebridge.donation.domain.pg.model.PgFlowType;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

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

    public static PaymentResponse from(ConfirmPaymentResult result) {
        return new PaymentResponse(
                result.paymentId(),
                result.status(),
                null,
                null,
                null
        );
    }
}
