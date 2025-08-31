package com.donation.carebridge.payment.domain;

public enum PgFlowType {
    CLIENT_SDK {
        @Override public boolean isConfirmableFrom(PaymentStatus s) {
            return s == PaymentStatus.REQUIRES_ACTION;
        }
        @Override public String error(PaymentStatus s) {
            return "CLIENT_SDK requires REQUIRES_ACTION, but was " + s;
        }
    },
    REDIRECT {
        @Override public boolean isConfirmableFrom(PaymentStatus s) {
            return s == PaymentStatus.REQUIRES_ACTION;
        }
        @Override public String error(PaymentStatus s) {
            return "REDIRECT requires REQUIRES_ACTION, but was " + s;
        }
    },
    SERVER_ONLY {
        @Override public boolean isConfirmableFrom(PaymentStatus s) {
            return s == PaymentStatus.CREATED || s == PaymentStatus.REQUIRES_ACTION;
        }
        @Override public String error(PaymentStatus s) {
            return "SERVER_ONLY requires CREATED or REQUIRES_ACTION, but was " + s;
        }
    },
    NONE {
        @Override public boolean isConfirmableFrom(PaymentStatus s) { return false; }
        @Override public String error(PaymentStatus s) { return "Flow NONE is not confirmable"; }
    };

    public abstract boolean isConfirmableFrom(PaymentStatus status);
    public abstract String error(PaymentStatus status);
}