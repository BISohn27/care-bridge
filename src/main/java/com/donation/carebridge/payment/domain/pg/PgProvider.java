package com.donation.carebridge.payment.domain.pg;

import com.donation.carebridge.payment.domain.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "pg_providers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PgProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private PgProviderCode code;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PgProviderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_type", length = 20, nullable = false)
    private PgFlowType flowType;

    public void assertConfirmable(PaymentStatus status) {
        if (!flowType.isConfirmableFrom(status)) {
            throw new IllegalStateException(flowType.error(status));
        }
    }
}