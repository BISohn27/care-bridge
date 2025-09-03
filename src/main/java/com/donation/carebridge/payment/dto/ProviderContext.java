package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payment.domain.pg.PgEnvironment;

public record ProviderContext(
    String clientId,
    String secretKey,
    PgEnvironment environment) {
}
