package com.donation.carebridge.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    @DisplayName("Payment.create()로 생성 시 기본 상태는 CREATED이고 PG 기본값은 toss이다")
    void should_haveCreatedStatus_when_createdByFactory() {
        Payment payment = Payment.create("case-1", "donor-1", 1000, "KRW", "idem-123");

        assertThat(payment.isCreate()).isTrue();
        assertThat(payment.getPgProvider()).isEqualTo("toss");
        assertThat(payment.getReasonCode()).isNull();
        assertThat(payment.getReasonMessage()).isNull();
    }

    @Test
    @DisplayName("CREATED에서 markPaid() 호출 시 PAID로 전환되고 paidAt와 paymentId가 세팅된다")
    void should_transitionToPaid_when_markPaidOnCreated() {
        Payment payment = Payment.create("case-1", "donor-1", 1000, "KRW", "idem-123");

        payment.markPaid("pg-001");

        assertThat(payment.isPaid()).isTrue();
        assertThat(payment.getPgProvider()).isEqualTo("toss");
        assertThat(payment.getPgPaymentId()).isEqualTo("pg-001");
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("PG 실패 시 FAILED로 전환되고 Reason.pg 값이 세팅된다")
    void should_setFailedAndReason_when_failedFromPg() {
        Payment payment = Payment.create("case-1", "donor-1", 1000, "KRW", "idem-123");

        payment.markFailedFromPg("PG_TIMEOUT", "PG 응답 지연");

        assertThat(payment.isFailed()).isTrue();
        assertThat(payment.getReasonCode()).isEqualTo("PG_TIMEOUT");
        assertThat(payment.getReasonMessage()).isEqualTo("PG 응답 지연");
    }

    @Test
    @DisplayName("시스템 실패 시 FAILED로 전환되고 Reason.system 값이 세팅된다")
    void should_setFailedAndReason_when_failedFromSystem() {
        Payment payment = Payment.create("case-1", "donor-1", 1000, "KRW", "idem-123");

        payment.markFailedFromSystem("SYS_ERROR", "DB 오류");

        assertThat(payment.isFailed()).isTrue();
        assertThat(payment.getReasonCode()).isEqualTo("SYS_ERROR");
        assertThat(payment.getReasonMessage()).isEqualTo("DB 오류");
    }

    @Test
    @DisplayName("사용자 취소 시 CANCELLED로 전환되고 Reason.user 메시지가 세팅된다")
    void should_setCancelledAndReason_when_cancelByUser() {
        Payment payment = Payment.create("case-1", "donor-1", 1000, "KRW", "idem-123");

        payment.cancelByUser("USR_CANCEL", "사용자 취소");

        assertThat(payment.isCancelled()).isTrue();
        assertThat(payment.getReasonMessage()).isEqualTo("사용자 취소");
    }

    @Test
    @DisplayName("CREATED가 아닐 때 markPaid() 호출 시 예외가 발생한다")
    void should_throwException_when_markPaidOnNonCreated() {
        Payment payment = Payment.create("case-1", "donor-1", 1000, "KRW", "idem-123");
        payment.markFailedFromSystem("SYS_ERR", "시스템 오류");

        assertThatThrownBy(() -> payment.markPaid("pg-001"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only CREATED");
    }

    @Test
    @DisplayName("CREATED가 아닐 때 markFailedFromPg() 호출 시 예외가 발생한다")
    void should_throwException_when_failedFromPgOnNonCreated() {
        Payment p = Payment.create("c","d",1000,"KRW","idem");
        p.cancelByUser("USR_CANCEL","사용자 취소");
        assertThatThrownBy(() -> p.markFailedFromPg("PG_TIMEOUT","msg"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only CREATED");
    }

    @Test
    @DisplayName("CREATED가 아닐 때 markFailedFromSystem() 호출 시 예외가 발생한다")
    void should_throwException_when_failedFromSystemOnNonCreated() {
        Payment p = Payment.create("c","d",1000,"KRW","idem");
        p.markPaid("pg-1");
        assertThatThrownBy(() -> p.markFailedFromSystem("SYS_ERR","msg"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only CREATED");
    }

    @Test
    @DisplayName("CREATED가 아닐 때 cancelByUser() 호출 시 예외가 발생한다")
    void should_throwException_when_cancelByUserOnNonCreated() {
        Payment p = Payment.create("c","d",1000,"KRW","idem");
        p.markFailedFromSystem("SYS_ERR","msg");
        assertThatThrownBy(() -> p.cancelByUser("USR_CANCEL","사용자 취소"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only CREATED");
    }

    @Test
    @DisplayName("PG 실패 코드가 null/blank면 PG_UNKNOWN으로 정규화된다")
    void should_normalizeToPgUnknown_when_failedFromPgWithNullOrBlank() {
        Payment p1 = Payment.create("c","d",1000,"KRW","i1");
        p1.markFailedFromPg(null, "m1");
        assertThat(p1.getReasonCode()).isEqualTo("PG_UNKNOWN");

        Payment p2 = Payment.create("c","d",1000,"KRW","i2");
        p2.markFailedFromPg("   ", "m2");
        assertThat(p2.getReasonCode()).isEqualTo("PG_UNKNOWN");
    }

    @Test
    @DisplayName("SYSTEM 실패 코드가 null/blank면 SYS_UNKNOWN으로 정규화된다")
    void should_normalizeToSysUnknown_when_failedFromSystemWithNullOrBlank() {
        Payment p1 = Payment.create("c","d",1000,"KRW","i1");
        p1.markFailedFromSystem(null, "m1");
        assertThat(p1.getReasonCode()).isEqualTo("SYS_UNKNOWN");

        Payment p2 = Payment.create("c","d",1000,"KRW","i2");
        p2.markFailedFromSystem("  ", "m2");
        assertThat(p2.getReasonCode()).isEqualTo("SYS_UNKNOWN");
    }
}