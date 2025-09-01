package com.donation.carebridge.payment.domain;

import com.donation.carebridge.payment.domain.pg.PgFlowType;
import com.donation.carebridge.payment.domain.pg.PgProvider;
import com.donation.carebridge.payment.domain.pg.PgProviderCode;
import com.donation.carebridge.payment.domain.pg.PgProviderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class PgProviderTest {

    private PgProvider clientSdk() {
        return PgProvider.builder()
                .code(PgProviderCode.TOSS)              // 실제 코드
                .name("CLIENT_SDK")
                .status(PgProviderStatus.ACTIVE)
                .flowType(PgFlowType.CLIENT_SDK)
                .build();
    }

    private PgProvider serverOnly() {
        return PgProvider.builder()
                .code(PgProviderCode.TOSS)
                .name("SERVER_ONLY")
                .status(PgProviderStatus.ACTIVE)
                .flowType(PgFlowType.SERVER_ONLY)
                .build();
    }

    private PgProvider noneProvider() {
        return PgProvider.builder()
                .code(PgProviderCode.TOSS)
                .name("NONE")
                .status(PgProviderStatus.ACTIVE)
                .flowType(PgFlowType.NONE)
                .build();
    }

    // 1) CLIENT_SDK는 CREATED에서 거부되어야 한다
    @Test
    @DisplayName("CLIENT_SDK는 CREATED 상태에서 confirm 불가")
    void should_rejectConfirm_when_clientSdkAndCreated() {
        PgProvider p = clientSdk();
        assertThatThrownBy(() -> p.assertConfirmable(PaymentStatus.CREATED))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("CLIENT_SDK는 REQUIRES_ACTION 상태에서 confirm 가능")
    void should_allowConfirm_when_clientSdkAndRequiresAction() {
        PgProvider p = clientSdk();
        assertThatCode(() -> p.assertConfirmable(PaymentStatus.REQUIRES_ACTION))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("SERVER_ONLY는 CREATED 상태에서 confirm 가능")
    void should_allowConfirm_when_serverOnlyAndCreated() {
        PgProvider p = serverOnly();
        assertThatCode(() -> p.assertConfirmable(PaymentStatus.CREATED))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NONE은 어떤 상태에서도 confirm 불가")
    void should_rejectConfirm_when_flowIsNone() {
        PgProvider p = noneProvider();
        for (PaymentStatus status : PaymentStatus.values()) {
            assertThatThrownBy(() -> p.assertConfirmable(status))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}