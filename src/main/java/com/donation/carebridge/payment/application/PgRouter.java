package com.donation.carebridge.payment.application;

import com.donation.carebridge.payment.domain.Currency;
import com.donation.carebridge.payment.domain.pg.PgEnvironment;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PgRouter {

    private final Environment env;

    public ProviderSelection resolve(PgProviderCode explicit, PaymentMethod method, Currency currency, long amount) {

        PgProviderCode code = (explicit != null)
                ? explicit
                : PgProviderCode.TOSS;

        return new ProviderSelection(code, isProd() ? PgEnvironment.LIVE : PgEnvironment.TEST);
    }

    private boolean isProd() {
        for (String p : env.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(p)) return true;
        }

        for (String p : env.getDefaultProfiles()) {
            if ("prod".equalsIgnoreCase(p)) return true;
        }
        return false;
    }
}