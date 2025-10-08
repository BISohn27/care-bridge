package com.donation.carebridge.donation.domain.pg.out;

import com.donation.carebridge.donation.domain.pg.model.PgAccount;
import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PgAccountRepository extends JpaRepository<PgAccount, Long> {
    Optional<PgAccount> findByProviderIdAndEnvironment(Long providerId, PgEnvironment environment);
}
