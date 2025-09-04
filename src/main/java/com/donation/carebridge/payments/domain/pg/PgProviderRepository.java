package com.donation.carebridge.payments.domain.pg;

public interface PgProviderRepository {

    PgProvider findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
