package com.donation.carebridge.payments.infrastructure.pg.repository;

import com.donation.carebridge.payments.pg.model.PgEnvironment;
import com.donation.carebridge.payments.pg.model.PgProvider;
import com.donation.carebridge.payments.pg.model.PgProviderCode;
import com.donation.carebridge.payments.pg.model.PgProviderRepository;
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
