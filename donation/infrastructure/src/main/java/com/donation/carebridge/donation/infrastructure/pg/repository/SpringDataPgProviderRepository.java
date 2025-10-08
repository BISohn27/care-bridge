package com.donation.carebridge.donation.infrastructure.pg.repository;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPgProviderRepository extends JpaRepository<PgProvider, Long> {

    Optional<PgProvider> findByCodeAndAccounts_environment(PgProviderCode code, PgEnvironment env);
}
