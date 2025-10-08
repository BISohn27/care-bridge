package com.donation.carebridge.donation.infrastructure.pg.repository;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
import com.donation.carebridge.donation.domain.pg.out.PgProviderRepository;
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
