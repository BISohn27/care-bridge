package com.donation.carebridge.donation.domain.payment.application;

import com.donation.carebridge.donation.domain.payment.dto.PaymentMethod;
import com.donation.carebridge.donation.domain.payment.dto.ProviderSelection;
import com.donation.carebridge.donation.domain.payment.model.Currency;
import com.donation.carebridge.donation.domain.pg.model.PgEnvironment;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
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