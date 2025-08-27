package com.donation.carebridge.payment.domain;

import com.donation.carebridge.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
        @UniqueConstraint(name = "uq_payments_idem", columnNames = {"idempotency_key"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    private static final String DEFAULT_PG = "toss";

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "case_id", length = 36, nullable = false)
    private String caseId;

    @Column(name = "donor_id", length = 36)
    private String donorId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private PaymentStatus status;

    @Embedded
    private PgInfo pgInfo;

    @Embedded
    private Reason reason;

    @Column(name = "idempotency_key", length = 36, nullable = false, unique = true)
    private String idempotencyKey;

    @Getter
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    private Payment(String caseId, String donorId, int amount, String currency, String idempotencyKey) {
        this.id = UUID.randomUUID().toString();
        this.caseId = caseId;
        this.donorId = donorId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.CREATED;
        this.pgInfo = PgInfo.initial(DEFAULT_PG);
        this.idempotencyKey = idempotencyKey;
    }

    public static Payment create(String caseId, String donorId, int amount, String currency, String idempotencyKey) {
        return new Payment(caseId, donorId, amount, currency, idempotencyKey);
    }

    public void markPaid(String pgPaymentId) {
        requireCreated();
        this.status = PaymentStatus.PAID;
        this.pgInfo = this.pgInfo.approved(pgPaymentId);
        this.paidAt = LocalDateTime.now();
        onUpdate();
    }

    private void requireCreated() {
        if (this.status != PaymentStatus.CREATED) {
            throw new IllegalStateException("Only CREATED can transition");
        }
    }

    public void markFailedFromPg(String pgCode, String pgMessage) {
        requireCreated();
        this.status = PaymentStatus.FAILED;
        this.reason = Reason.pg(pgCode, pgMessage);
        onUpdate();
    }

    public void markFailedFromSystem(String code, String message) {
        requireCreated();
        this.status = PaymentStatus.FAILED;
        this.reason = Reason.system(code, message);
        onUpdate();
    }

    public void cancelByUser(String code, String message) {
        requireCreated();
        this.status = PaymentStatus.CANCELLED;
        this.reason = Reason.user(code, message);
        onUpdate();
    }

    public boolean isCreate() {
        return status == PaymentStatus.CREATED;
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

    public String getPgProvider() {
        return pgInfo.getProvider();
    }

    public String getPgPaymentId() {
        return pgInfo.getPaymentId();
    }
}