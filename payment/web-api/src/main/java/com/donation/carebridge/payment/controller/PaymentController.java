package com.donation.carebridge.payment.controller;

import com.donation.carebridge.payment.dto.PaymentResponse;
import com.donation.carebridge.payment.dto.ResponseMessage;
import com.donation.carebridge.payments.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.payments.payment.dto.CreatePaymentResult;
import com.donation.carebridge.payments.payment.usecase.CreatePaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    @PostMapping
    public ResponseEntity<ResponseMessage<PaymentResponse>> createPayment(
            @RequestBody CreatePaymentRequest request) {
        CreatePaymentResult createPaymentResult = createPaymentUseCase.create(request);
        PaymentResponse paymentResponse = PaymentResponse.from(createPaymentResult);
        return ResponseEntity.ok(ResponseMessage.success(paymentResponse));
    }
}
