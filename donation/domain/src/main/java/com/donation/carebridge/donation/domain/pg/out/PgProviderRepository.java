package com.donation.carebridge.donation.domain.pg.out;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

import java.util.Optional;

public interface PgProviderRepository {

    Optional<PgProvider> findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
