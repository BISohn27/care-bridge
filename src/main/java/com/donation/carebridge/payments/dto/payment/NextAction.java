package com.donation.carebridge.payments.dto.payment;

import com.donation.carebridge.payments.domain.pg.PgFlowType;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;

public record NextAction(
    PgFlowType flowType,
    PgProviderCode provider,
    Object payload) {
}