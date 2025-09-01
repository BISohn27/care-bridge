package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payment.domain.pg.PgFlowType;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;

public record NextAction(
    PgFlowType flowType,
    PgProviderCode provider,
    Object payload) {
}