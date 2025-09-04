package com.donation.carebridge.payments.application.payment;

import com.donation.carebridge.payments.domain.payment.Currency;
import com.donation.carebridge.payments.domain.pg.PgEnvironment;
import com.donation.carebridge.payments.domain.pg.PgProviderCode;
import com.donation.carebridge.payments.dto.payment.PaymentMethod;
import com.donation.carebridge.payments.dto.payment.ProviderSelection;
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