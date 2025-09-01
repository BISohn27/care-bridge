package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.pg.PgFlowType;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;

public record NextAction(
    PgFlowType flowType,
    PgProviderCode provider,
    Object payload) {
}