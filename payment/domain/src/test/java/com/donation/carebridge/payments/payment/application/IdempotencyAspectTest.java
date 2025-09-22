package com.donation.carebridge.payments.payment.application;

import com.donation.carebridge.payments.payment.annotation.IdempotencyCheck;
import com.donation.carebridge.payments.payment.exception.PaymentException;
import com.donation.carebridge.payments.payment.model.IdempotencyKeyed;
import com.donation.carebridge.payments.payment.out.IdempotencyRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    IdempotencyAspect.class,
    IdempotencyAspectTest.TestPaymentService.class,
    IdempotencyAspectTest.IdempotencyTestConfig.class
})
class IdempotencyAspectTest {

    @Autowired
    private TestPaymentService testPaymentService;

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(idempotencyRepository);
    }

    @Test
    @DisplayName("멱등성 키 예약 성공 시 정상 처리")
    void shouldProcessWhenIdempotencyKeyReservedSuccessfully() {
        String paymentId = "pay_12345";
        TestPaymentRequest request = new TestPaymentRequest(paymentId, 1000L);

        when(idempotencyRepository.reserveIdempotencyKey("payment", paymentId))
                .thenReturn(true);

        String result = testPaymentService.processPayment(request);

        assertThat(result).isEqualTo("Payment processed: 1000");
        verify(idempotencyRepository).reserveIdempotencyKey("payment", paymentId);
        verify(idempotencyRepository).completeIdempotencyKey("payment", paymentId);
        verify(idempotencyRepository, never()).cancelIdempotencyKey(anyString(), anyString());
    }

    @Test
    @DisplayName("멱등성 키 예약 실패 시 예외 발생")
    void shouldThrowExceptionWhenIdempotencyKeyAlreadyExists() {
        String paymentId = "pay_12345";
        TestPaymentRequest request = new TestPaymentRequest(paymentId, 1000L);

        when(idempotencyRepository.reserveIdempotencyKey("payment", paymentId))
                .thenReturn(false);

        assertThatThrownBy(() -> testPaymentService.processPayment(request))
                .isInstanceOf(PaymentException.class);

        verify(idempotencyRepository).reserveIdempotencyKey("payment", paymentId);
        verify(idempotencyRepository, never()).completeIdempotencyKey(anyString(), anyString());
        verify(idempotencyRepository, never()).cancelIdempotencyKey(anyString(), anyString());
    }

    @Test
    @DisplayName("비즈니스 로직 실행 중 예외 발생 시 멱등성 키 취소")
    void shouldCancelIdempotencyKeyWhenBusinessLogicFails() {
        String refundId = "FAIL";
        TestRefundRequest request = new TestRefundRequest(refundId);

        when(idempotencyRepository.reserveIdempotencyKey("refund", refundId))
                .thenReturn(true);

        assertThatThrownBy(() -> testPaymentService.processRefund(request))
                .isInstanceOf(RuntimeException.class);

        verify(idempotencyRepository).reserveIdempotencyKey("refund", refundId);
        verify(idempotencyRepository).cancelIdempotencyKey("refund", refundId);
        verify(idempotencyRepository, never()).completeIdempotencyKey(anyString(), anyString());
    }

    @Test
    @DisplayName("멱등성 키가 빈 값일 때 멱등성 체크 건너뛰기")
    void shouldSkipIdempotencyCheckWhenKeyIsEmpty() {
        TestPaymentRequest request = new TestPaymentRequest("", 1000L);

        String result = testPaymentService.processPayment(request);

        assertThat(result).isEqualTo("Payment processed: 1000");
        verifyNoInteractions(idempotencyRepository);
    }

    @Test
    @DisplayName("멱등성 키가 null일 때 멱등성 체크 건너뛰기")
    void shouldSkipIdempotencyCheckWhenKeyIsNull() {
        TestPaymentRequest request = new TestPaymentRequest(null, 1000L);

        String result = testPaymentService.processPayment(request);

        assertThat(result).isEqualTo("Payment processed: 1000");
        verifyNoInteractions(idempotencyRepository);
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class IdempotencyTestConfig {

        @Bean
        @Primary
        public IdempotencyRepository mockIdempotencyRepository() {
            return Mockito.mock(IdempotencyRepository.class);
        }
    }

    @Service
    static class TestPaymentService {

        @IdempotencyCheck(prefix = "payment")
        String processPayment(TestPaymentRequest request) {
            return "Payment processed: " + request.getAmount();
        }

        @IdempotencyCheck(prefix = "refund")
        String processRefund(TestRefundRequest request) {
            if ("FAIL".equals(request.getRefundId())) {
                throw new RuntimeException("Refund failed");
            }
            return "Refund processed: " + request.getRefundId();
        }

        String processWithoutIdempotencyKey(String message) {
            return "Processed: " + message;
        }
    }

    @Getter
    @AllArgsConstructor
    static class TestNormalRequest {
        private String message;
    }

    @Getter
    @AllArgsConstructor
    static class TestPaymentRequest implements IdempotencyKeyed {
        private String paymentId;
        private Long amount;

        @Override
        public String idempotencyKey() {
            return paymentId;
        }
    }

    @Getter
    @AllArgsConstructor
    static class TestRefundRequest implements IdempotencyKeyed {
        private String refundId;

        @Override
        public String idempotencyKey() {
            return refundId;
        }
    }
}