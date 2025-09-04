package com.donation.carebridge.payments.dto.payment;

import com.donation.carebridge.payments.domain.pg.PgEnvironment;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;

public record ProviderSelection(PgProviderCode pgProviderCode, PgEnvironment env) {
}
