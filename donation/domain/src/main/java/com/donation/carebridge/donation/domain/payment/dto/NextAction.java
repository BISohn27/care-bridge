package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.donation.domain.pg.model.PgFlowType;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

public record NextAction(
    PgFlowType flowType,
    PgProviderCode provider,
    Object payload) {
}