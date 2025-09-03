package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.pg.PgAccount;
import com.donation.carebridge.payment.domain.pg.PgEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PgAccountRepository extends JpaRepository<PgAccount, Long> {
    Optional<PgAccount> findByProviderIdAndEnvironment(Long providerId, PgEnvironment environment);
}
