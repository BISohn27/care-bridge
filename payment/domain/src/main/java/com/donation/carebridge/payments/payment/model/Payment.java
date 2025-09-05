package com.donation.carebridge.payments.payment.model;

import com.donation.carebridge.common.domain.BaseTimeEntity;
import com.donation.carebridge.payments.pg.model.PgProvider;
import com.donation.carebridge.payments.pg.model.PgProviderCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_payments_pg",   columnNames = {"pg_provider_id", "pg_payment_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id @Getter
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "case_id", length = 36, nullable = false)
    private String caseId;

    @Column(name = "donor_id", length = 36)
    private String donorId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private PaymentStatus status;

    @Column(name = "idempotency_key", length = 36, nullable = false, unique = true)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pg_provider_id", nullable = false)
    private PgProvider pgProvider;

    @Column(name = "pg_payment_id", length = 100)
    private String pgPaymentId;

    @Embedded
    private Reason reason;

    @Getter
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    private Payment(
            String caseId,
            String donorId,
            long amount,
            Currency currency,
            String idempotencyKey,
            PgProvider pgProvider) {
        this.id = UUID.randomUUID().toString();
        this.caseId = caseId;
        this.donorId = donorId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.CREATED;
        this.idempotencyKey = idempotencyKey;
        this.pgProvider = pgProvider;
    }

    public static Payment create(
            String caseId,
            String donorId,
            long amount,
            Currency currency,
            String idempotencyKey,
            PgProvider pgProvider) {
        return new Payment(caseId, donorId, amount, currency, idempotencyKey, pgProvider);
    }

    public void markRequiresAction() {
        if (this.status != PaymentStatus.CREATED) {
            throw new IllegalStateException("Only CREATED can transition to REQUIRES_ACTION, but was " + this.status);
        }
        this.status = PaymentStatus.REQUIRES_ACTION;
    }

    public void markPaid(String pgPaymentId) {
        requireConfirmable();
        this.status = PaymentStatus.PAID;
        this.pgPaymentId = pgPaymentId;
        this.paidAt = LocalDateTime.now();
    }

    private void requireConfirmable() {
        if (pgProvider == null) {
            throw new IllegalStateException("PG provider is not set");
        }
        pgProvider.assertConfirmable(this.status);
    }

    public void markFailedFromPg(String pgCode, String pgMessage) {
        requireConfirmable();
        this.status = PaymentStatus.FAILED;
        this.reason = Reason.pg(pgCode, pgMessage);
    }

    public void markFailedFromSystem(String code, String message) {
        requireConfirmable();
        this.status = PaymentStatus.FAILED;
        this.reason = Reason.system(code, message);
    }

    public void cancelByUser(String code, String message) {
        requireConfirmable();
        this.status = PaymentStatus.CANCELLED;
        this.reason = Reason.user(code, message);
    }

    public boolean isCreate() {
        return status == PaymentStatus.CREATED;
    }

    public boolean isRequireAction() {
        return status == PaymentStatus.REQUIRES_ACTION;
    }

    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean isCancelled() {
        return status == PaymentStatus.CANCELLED;
    }

    public String getReasonCode() {
        return reason == null ? null : reason.getCode();
    }

    public String getReasonMessage() {
        return reason == null ? null : reason.getMessage();
    }

    public PgProviderCode getPgProvider() {
        return pgProvider.getCode();
    }

    public String getPgPaymentId() {
        return pgPaymentId;
    }
}