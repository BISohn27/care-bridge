package com.donation.carebridge.payments.application.pg;

import com.donation.carebridge.payments.domain.pg.PgEnvironment;
import com.donation.carebridge.payments.domain.pg.PgProvider;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import com.donation.carebridge.payments.domain.pg.PgProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PgProviderService {

    private final PgProviderRepository pgProviderRepository;

    public PgProvider getProvider(PgProviderCode pgProviderCode, PgEnvironment pgEnvironment) {
        return pgProviderRepository.findByCodeAndAccountStatus(pgProviderCode, pgEnvironment)
                .orElseThrow(() -> new IllegalArgumentException("No provider found for " + pgProviderCode));
    }
}
