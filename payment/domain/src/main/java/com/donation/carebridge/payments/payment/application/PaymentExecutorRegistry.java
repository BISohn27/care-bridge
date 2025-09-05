package com.donation.carebridge.payments.payment.application;

import com.donation.carebridge.payments.payment.model.PaymentExecutor;
import com.donation.carebridge.payments.pg.model.PgProviderCode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PaymentExecutorRegistry {

    private final Map<PgProviderCode, PaymentExecutor> executors;

    public PaymentExecutorRegistry(List<PaymentExecutor> paymentExecutors) {
        executors = new EnumMap<>(PgProviderCode.class);
        paymentExecutors.forEach(executor -> executors.put(executor.key(), executor));
    }

    public PaymentExecutor get(PgProviderCode code) {
        return Optional.ofNullable(executors.get(code))
                .orElseThrow(() -> new IllegalArgumentException("No executor registered for " + code));
    }
}
