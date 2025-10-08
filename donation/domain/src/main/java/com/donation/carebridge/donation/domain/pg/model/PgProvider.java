package com.donation.carebridge.donation.domain.pg.model;

import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private PgStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_type", length = 20, nullable = false)
    private PgFlowType flowType;

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private List<PgAccount> accounts = new ArrayList<>();

    public void assertConfirmable(PaymentStatus status) {
        if (!flowType.isConfirmableFrom(status)) {
            throw new IllegalStateException(flowType.error(status));
        }
    }
}