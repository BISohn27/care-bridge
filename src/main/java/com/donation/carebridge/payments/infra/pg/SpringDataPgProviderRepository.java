package com.donation.carebridge.payments.infra.pg;

import com.donation.carebridge.payments.domain.pg.PgEnvironment;
import com.donation.carebridge.payments.domain.pg.PgProvider;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPgProviderRepository extends JpaRepository<PgProvider, Long> {

    Optional<PgProvider> findByCodeAndAccounts_environment(PgProviderCode code, PgEnvironment env);
}
