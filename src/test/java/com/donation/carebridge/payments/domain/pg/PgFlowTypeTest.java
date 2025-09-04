package com.donation.carebridge.payments.domain.pg;

import com.donation.carebridge.payments.domain.payment.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgFlowTypeTest {

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"REQUIRES_ACTION"})
    @DisplayName("CLIENT_SDK는 REQUIRES_ACTION 상태에서만 confirm 가능하다")
    void should_allowConfirm_when_clientSdkAndRequiresAction(PaymentStatus status) {
        assertThat(PgFlowType.CLIENT_SDK.isConfirmableFrom(status)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"REQUIRES_ACTION"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("CLIENT_SDK는 REQUIRES_ACTION 이외 상태에서는 confirm 불가능하다")
    void should_rejectConfirm_when_clientSdkAndNotRequiresAction(PaymentStatus status) {
        assertThat(PgFlowType.CLIENT_SDK.isConfirmableFrom(status)).isFalse();
        assertThatThrownBy(() -> {
            if (!PgFlowType.CLIENT_SDK.isConfirmableFrom(status)) {
                throw new IllegalStateException(PgFlowType.CLIENT_SDK.error(status));
            }
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not confirmable")
                .hasMessageContaining("flow=CLIENT_SDK")
                .hasMessageContaining("allowed=[REQUIRES_ACTION]")
                .hasMessageContaining("current=" + status);
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"REQUIRES_ACTION"})
    @DisplayName("REDIRECT는 REQUIRES_ACTION 상태에서만 confirm 가능하다")
    void should_allowConfirm_when_redirectAndRequiresAction(PaymentStatus status) {
        assertThat(PgFlowType.REDIRECT.isConfirmableFrom(status)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"REQUIRES_ACTION"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("REDIRECT는 REQUIRES_ACTION 이외 상태에서는 confirm 불가능하다")
    void should_rejectConfirm_when_redirectAndNotRequiresAction(PaymentStatus status) {
        assertThat(PgFlowType.REDIRECT.isConfirmableFrom(status)).isFalse();
        assertThatThrownBy(() -> {
            if (!PgFlowType.REDIRECT.isConfirmableFrom(status)) {
                throw new IllegalStateException(PgFlowType.REDIRECT.error(status));
            }
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not confirmable")
                .hasMessageContaining("flow=REDIRECT")
                .hasMessageContaining("allowed=[REQUIRES_ACTION]")
                .hasMessageContaining("current=" + status);
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"CREATED","REQUIRES_ACTION"})
    @DisplayName("SERVER_ONLY는 CREATED/REQUIRES_ACTION 상태에서 confirm 가능하다")
    void should_allowConfirm_when_serverOnlyAndCreatedOrRequiresAction(PaymentStatus status) {
        assertThat(PgFlowType.SERVER_ONLY.isConfirmableFrom(status)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"CREATED","REQUIRES_ACTION"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("SERVER_ONLY는 CREATED/REQUIRES_ACTION 이외 상태에서는 confirm 불가능하다")
    void should_rejectConfirm_when_serverOnlyAndNotCreatedOrRequiresAction(PaymentStatus status) {
        assertThat(PgFlowType.SERVER_ONLY.isConfirmableFrom(status)).isFalse();
        assertThatThrownBy(() -> {
            if (!PgFlowType.SERVER_ONLY.isConfirmableFrom(status)) {
                throw new IllegalStateException(PgFlowType.SERVER_ONLY.error(status));
            }
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not confirmable")
                .hasMessageContaining("flow=SERVER_ONLY")
                .hasMessageContaining("allowed=[CREATED, REQUIRES_ACTION]")
                .hasMessageContaining("current=" + status);
    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    @DisplayName("NONE 플로우는 어떤 상태에서도 confirm 불가능하다")
    void should_rejectConfirm_when_flowIsNone(PaymentStatus status) {
        assertThat(PgFlowType.NONE.isConfirmableFrom(status)).isFalse();
        assertThatThrownBy(() -> {
            if (!PgFlowType.NONE.isConfirmableFrom(status)) {
                throw new IllegalStateException(PgFlowType.NONE.error(status));
            }
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not confirmable")
                .hasMessageContaining("flow=NONE")
                .hasMessageContaining("allowed=[]")
                .hasMessageContaining("current=" + status);
    }
}