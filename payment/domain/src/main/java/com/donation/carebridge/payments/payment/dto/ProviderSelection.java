package com.donation.carebridge.payments.payment.dto;

import com.donation.carebridge.payments.pg.model.PgEnvironment;
import com.donation.carebridge.payments.pg.model.PgProviderCode;

public record ProviderSelection(PgProviderCode pgProviderCode, PgEnvironment env) {
}
