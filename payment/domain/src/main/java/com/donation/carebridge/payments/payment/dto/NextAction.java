package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.pg.model.PgFlowType;
import com.donation.carebridge.payments.pg.model.PgProviderCode;

public record NextAction(
    PgFlowType flowType,
    PgProviderCode provider,
    Object payload) {
}