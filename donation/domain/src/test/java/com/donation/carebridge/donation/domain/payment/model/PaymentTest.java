package com.donation.carebridge.donation.domain.payment.model;

import com.donation.carebridge.donation.domain.payment.exception.PaymentException;
import com.donation.carebridge.donation.domain.payment.model.Currency;
import com.donation.carebridge.donation.domain.payment.model.Payment;
import com.donation.carebridge.donation.domain.pg.model.PgFlowType;
import com.donation.carebridge.donation.domain.pg.model.PgProvider;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
import com.donation.carebridge.donation.domain.pg.model.PgStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    private PgProvider tossProvider() {
        return new PgProvider(
                null,
                PgProviderCode.TOSS,
                "Toss Payments",
                PgStatus.ACTIVE,
                PgFlowType.SERVER_ONLY,
                Collections.emptyList()
        );
    }

    @Test
    @DisplayName("Payment.create()로 생성 시 기본 상태는 CREATED이고 PG는 주입된 Provider(TOSS)이다")
    void should_haveCreatedStatus_when_createdByFactory() {
        PgProvider provider = tossProvider();

        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);

        assertThat(payment.isCreate()).isTrue();
        assertThat(payment.getPgProvider()).isEqualTo(PgProviderCode.TOSS);
        assertThat(payment.getReasonCode()).isNull();
        assertThat(payment.getReasonMessage()).isNull();
    }

    @Test
    @DisplayName("CREATED 상태에서 markRequiresAction() 호출 시 REQUIRES_ACTION으로 전환된다")
    void should_transitionToRequiresAction_when_created() {
        PgProvider provider = tossProvider(); // flowType=CLIENT_SDK
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);

        payment.markRequiresAction();

        assertThat(payment.isRequireAction()).isTrue();
    }

    @Test
    @DisplayName("CREATED가 아닌 상태에서 markRequiresAction() 호출 시 예외가 발생한다")
    void should_throwException_when_markRequiresActionOnNonCreated() {
        PgProvider provider = tossProvider();
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);
        payment.markFailedFromSystem("SYS_ERR", "시스템 오류");

        assertThatThrownBy(payment::markRequiresAction)
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("CREATED");
    }

    @Test
    @DisplayName("프로바이더 정책상 승인 가능한 상태에서 markPaid() 호출 시 PAID로 전환되고 pgPaymentId와 paidAt이 세팅된다")
    void should_transitionToPaid_when_markPaidOnCreated() {
        PgProvider provider = tossProvider();
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);

        payment.markPaid("pg-001");

        assertThat(payment.isPaid()).isTrue();
        assertThat(payment.getPgProvider()).isEqualTo(PgProviderCode.TOSS);
        assertThat(payment.getPgPaymentId()).isEqualTo("pg-001");
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("PG 실패 시 FAILED로 전환되고 Reason.pg 값이 세팅된다")
    void should_setFailedAndReason_when_failedFromPg() {
        PgProvider provider = tossProvider();
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);

        payment.markFailedFromPg("PG_TIMEOUT", "PG 응답 지연");

        assertThat(payment.isFailed()).isTrue();
        assertThat(payment.getReasonCode()).isEqualTo("PG_TIMEOUT");
        assertThat(payment.getReasonMessage()).isEqualTo("PG 응답 지연");
    }

    @Test
    @DisplayName("시스템 실패 시 FAILED로 전환되고 Reason.system 값이 세팅된다")
    void should_setFailedAndReason_when_failedFromSystem() {
        PgProvider provider = tossProvider();
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);

        payment.markFailedFromSystem("SYS_ERROR", "DB 오류");

        assertThat(payment.isFailed()).isTrue();
        assertThat(payment.getReasonCode()).isEqualTo("SYS_ERROR");
        assertThat(payment.getReasonMessage()).isEqualTo("DB 오류");
    }

    @Test
    @DisplayName("사용자 취소 시 CANCELLED로 전환되고 Reason.user 메시지가 세팅된다")
    void should_setCancelledAndReason_when_cancelByUser() {
        PgProvider provider = tossProvider();
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);

        payment.cancelByUser("USR_CANCEL", "사용자 취소");

        assertThat(payment.isCancelled()).isTrue();
        assertThat(payment.getReasonMessage()).isEqualTo("사용자 취소");
    }

    @Test
    @DisplayName("현재 상태에서 이 프로바이더 정책상 markPaid()가 허용되지 않으면 예외가 발생한다")
    void should_throwException_when_markPaid_notConfirmableByProviderPolicy() {
        PgProvider provider = tossProvider(); // CLIENT_SDK → REQUIRES_ACTION 필요
        Payment payment = Payment.create("donation-1", 1000L, Currency.KRW, "idem-123", provider);
        payment.markFailedFromSystem("SYS_ERR", "시스템 오류"); // FAILED

        assertThatThrownBy(() -> payment.markPaid("pg-001"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("현재 상태에서 이 프로바이더 정책상 markFailedFromPg()가 허용되지 않으면 예외가 발생한다")
    void should_throwException_when_failedFromPg_notAllowedInCurrentState() {
        PgProvider provider = tossProvider();
        Payment p = Payment.create("donation-1", 1000L, Currency.KRW, "idem", provider);
        p.cancelByUser("USR_CANCEL","사용자 취소");

        assertThatThrownBy(() -> p.markFailedFromPg("PG_TIMEOUT","msg"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("현재 상태에서 이 프로바이더 정책상 markFailedFromSystem()이 허용되지 않으면 예외가 발생한다")
    void should_throwException_when_failedFromSystem_notAllowedInCurrentState() {
        PgProvider provider = tossProvider();
        Payment p = Payment.create("donation-1", 1000L, Currency.KRW, "idem", provider);
        p.markPaid("pg-1");

        assertThatThrownBy(() -> p.markFailedFromSystem("SYS_ERR","msg"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("PG 실패 코드가 null/blank면 PG_UNKNOWN으로 정규화된다")
    void should_normalizeToPgUnknown_when_failedFromPgWithNullOrBlank() {
        PgProvider provider = tossProvider();

        Payment p1 = Payment.create("donation-1", 1000L, Currency.KRW, "i1", provider);
        p1.markFailedFromPg(null, "m1");
        assertThat(p1.getReasonCode()).isEqualTo("PG_UNKNOWN");

        Payment p2 = Payment.create("donation-2", 1000L, Currency.KRW, "i2", provider);
        p2.markFailedFromPg("   ", "m2");
        assertThat(p2.getReasonCode()).isEqualTo("PG_UNKNOWN");
    }

    @Test
    @DisplayName("SYSTEM 실패 코드가 null/blank면 SYS_UNKNOWN으로 정규화된다")
    void should_normalizeToSysUnknown_when_failedFromSystemWithNullOrBlank() {
        PgProvider provider = tossProvider();

        Payment p1 = Payment.create("donation-1", 1000L, Currency.KRW, "i1", provider);
        p1.markFailedFromSystem(null, "m1");
        assertThat(p1.getReasonCode()).isEqualTo("SYS_UNKNOWN");

        Payment p2 = Payment.create("donation-2", 1000L, Currency.KRW, "i2", provider);
        p2.markFailedFromSystem("  ", "m2");
        assertThat(p2.getReasonCode()).isEqualTo("SYS_UNKNOWN");
    }
}