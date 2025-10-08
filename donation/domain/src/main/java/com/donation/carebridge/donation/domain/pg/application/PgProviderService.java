package com.donation.carebridge.donation.domain.pg.application;

import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
import com.donation.carebridge.donation.domain.pg.out.PgProviderRepository;
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
