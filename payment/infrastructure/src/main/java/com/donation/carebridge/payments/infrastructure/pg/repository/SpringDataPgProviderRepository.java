package com.donation.carebridge.payments.infrastructure.pg.repository;

import com.donation.carebridge.payments.pg.model.PgEnvironment;
import com.donation.carebridge.payments.pg.model.PgProvider;
import com.donation.carebridge.payments.pg.model.PgProviderCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPgProviderRepository extends JpaRepository<PgProvider, Long> {

    Optional<PgProvider> findByCodeAndAccounts_environment(PgProviderCode code, PgEnvironment env);
}
