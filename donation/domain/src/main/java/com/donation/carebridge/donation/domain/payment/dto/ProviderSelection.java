package com.donation.carebridge.donation.domain.payment.dto;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

public record ProviderSelection(PgProviderCode pgProviderCode, PgEnvironment env) {
}
