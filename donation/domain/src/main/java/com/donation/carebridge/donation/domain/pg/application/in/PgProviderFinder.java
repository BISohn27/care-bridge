package com.donation.carebridge.donation.domain.pg.application.in;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;

public interface PgProviderFinder {

    PgProvider find(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment);
}
