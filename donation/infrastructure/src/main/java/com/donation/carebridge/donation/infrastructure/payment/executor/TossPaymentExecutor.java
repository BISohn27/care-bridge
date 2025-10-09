package com.donation.carebridge.donation.infrastructure.payment.executor;

import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentCommand;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentCommand;
import com.donation.carebridge.donation.domain.payment.dto.NextAction;
import com.donation.carebridge.donation.domain.payment.dto.PaymentExecutionResult;
import com.donation.carebridge.donation.domain.payment.dto.ProviderContext;
import com.donation.carebridge.donation.domain.payment.model.PaymentStatus;
import com.donation.carebridge.donation.domain.payment.application.out.PaymentExecutor;
import com.donation.carebridge.donation.domain.pg.model.PgProviderCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TossPaymentExecutor implements PaymentExecutor {

    @Override
    public PgProviderCode key() {
        return PgProviderCode.TOSS;
    }

    @Override
    public PaymentExecutionResult prepareCreateSession(CreatePaymentCommand command, ProviderContext providerContext) {
        return new PaymentExecutionResult(
                new NextAction(
                    providerContext.flowType(),
                    key(),
                    Map.of("clientId", providerContext.clientId())));
    }

    @Override
    public PaymentExecutionResult confirmPayment(ConfirmPaymentCommand command, ProviderContext providerContext) {
        // TODO: 실제 토스 페이먼츠 API 호출 구현 필요
        // 현재는 스텁 구현으로 항상 성공 처리
        log.info("Confirming payment with Toss: paymentId={}, amount={}", 
                command.paymentId(), command.amount());
        
        String pgPaymentId = (String) command.providerPayload().get("paymentKey");
        if (pgPaymentId == null) {
            return new PaymentExecutionResult(
                "MISSING_PAYMENT_KEY",
                "paymentKey is required in payload"
            );
        }
        
        return new PaymentExecutionResult(
            PaymentStatus.PAID,
            null,
            null
        );
    }
}
