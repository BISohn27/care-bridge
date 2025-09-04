package com.donation.carebridge.payments.dto.payment;

import com.donation.carebridge.payments.domain.pg.PgEnvironment;
import com.donation.carebridge.payments.domain.pg.PgFlowType;

public record ProviderContext(
    PgFlowType flowType,
    String clientId,
    String secretKey,
    PgEnvironment environment) {
}
