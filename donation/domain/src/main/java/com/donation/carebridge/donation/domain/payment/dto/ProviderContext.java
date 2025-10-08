package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgFlowType;

public record ProviderContext(
    PgFlowType flowType,
    String clientId,
    String secretKey,
    PgEnvironment environment) {
}
