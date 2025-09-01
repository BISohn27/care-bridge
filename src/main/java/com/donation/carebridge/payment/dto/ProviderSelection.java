package com.donation.carebridge.payment.dto;

import com.donation.carebridge.payment.domain.pg.PgEnvironment;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;

public record ProviderSelection(PgProviderCode pgProviderCode, PgEnvironment env) {
}
