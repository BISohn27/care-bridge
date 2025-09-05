package com.donation.carebridge.payments.pg.model;

import java.util.Optional;

public interface PgProviderRepository {

    Optional<PgProvider> findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
