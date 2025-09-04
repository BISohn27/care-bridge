package com.donation.carebridge.payments.domain.pg;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PgAccountRepository extends JpaRepository<PgAccount, Long> {
    Optional<PgAccount> findByProviderIdAndEnvironment(Long providerId, PgEnvironment environment);
}
