package com.donation.carebridge.payments.domain.pg;

import com.donation.carebridge.payments.domain.payment.PaymentStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum PgFlowType {
    CLIENT_SDK(PaymentStatus.REQUIRES_ACTION),
    REDIRECT(PaymentStatus.REQUIRES_ACTION),
    SERVER_ONLY(PaymentStatus.CREATED, PaymentStatus.REQUIRES_ACTION),
    NONE();

    private final Set<PaymentStatus> confirmableStatuses;

    PgFlowType(PaymentStatus... allowed) {
        this.confirmableStatuses = unmodifiableEnumSet(allowed);
    }

    public boolean isConfirmableFrom(PaymentStatus status) {
        return confirmableStatuses.contains(status);
    }

    public String error(PaymentStatus current) {
        return String.format(
                "Not confirmable: flow=%s, allowed=%s, current=%s",
                name(),
                confirmableStatuses,
                current
        );
    }

    private static Set<PaymentStatus> unmodifiableEnumSet(PaymentStatus... allowed) {
        if (allowed == null || allowed.length == 0) {
            return Collections.unmodifiableSet(EnumSet.noneOf(PaymentStatus.class));
        }

        EnumSet<PaymentStatus> set = EnumSet.of(allowed[0], Arrays.copyOfRange(allowed, 1, allowed.length));
        return Collections.unmodifiableSet(set);
    }
}