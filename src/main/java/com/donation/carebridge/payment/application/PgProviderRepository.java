package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.pg.PgProvider;
import com.donation.carebridge.payment.domain.pg.PgEnvironment;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;

public interface PgProviderRepository {

    PgProvider findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
