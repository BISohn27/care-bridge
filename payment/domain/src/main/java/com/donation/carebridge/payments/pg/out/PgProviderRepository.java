package com.donation.carebridge.payments.pg.out;

import com.donation.carebridge.payments.pg.model.PgEnvironment;
import com.donation.carebridge.payments.pg.model.PgProvider;
import com.donation.carebridge.payments.pg.model.PgProviderCode;

import java.util.Optional;

public interface PgProviderRepository {

    Optional<PgProvider> findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
