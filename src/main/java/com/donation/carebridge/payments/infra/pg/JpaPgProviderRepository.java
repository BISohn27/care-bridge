package com.donation.carebridge.payments.infra.pg;

import com.donation.carebridge.payments.domain.pg.PgEnvironment;
import com.donation.carebridge.payments.domain.pg.PgProvider;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import com.donation.carebridge.payments.domain.pg.PgProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPgProviderRepository implements PgProviderRepository {

    private final SpringDataPgProviderRepository dataRepository;

    @Override
    public Optional<PgProvider> findByCodeAndAccountStatus(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment) {
        return dataRepository.findByCodeAndAccounts_environment(pgProviderCode, pgEnvironment);
    }
}
