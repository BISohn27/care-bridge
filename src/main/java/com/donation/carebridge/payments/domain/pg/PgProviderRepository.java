package com.donation.carebridge.payments.domain.pg;

import java.util.Optional;

public interface PgProviderRepository {

    Optional<PgProvider> findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
