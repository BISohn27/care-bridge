package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.pg.model.PgEnvironment;
import com.donation.carebridge.payments.pg.model.PgFlowType;

public record ProviderContext(
    PgFlowType flowType,
    String clientId,
    String secretKey,
    PgEnvironment environment) {
}
