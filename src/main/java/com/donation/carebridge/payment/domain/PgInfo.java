package com.donation.carebridge.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PgInfo {

    @Column(name = "pg_provider", length = 32, nullable = false)
    private String provider;

    @Column(name = "pg_payment_id", length = 64)
    private String paymentId;

    private PgInfo(String provider, String paymentId) {
        this.provider = provider;
        this.paymentId = paymentId;
    }

    public static PgInfo of(String provider, String paymentId) {
        return new PgInfo(provider, paymentId);
    }

    public static PgInfo initial(String provider) {
        return new PgInfo(provider, null);
    }

    public PgInfo approved(String paymentId) {
        return new PgInfo(this.provider, paymentId);
    }
}
