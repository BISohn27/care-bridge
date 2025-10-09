package com.donation.carebridge.donation.webapi.payment.controller;

import com.donation.carebridge.donation.webapi.payment.dto.PaymentResponse;
import com.donation.carebridge.donation.webapi.payment.dto.ResponseMessage;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.ConfirmPaymentResult;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentRequest;
import com.donation.carebridge.donation.domain.payment.dto.CreatePaymentResult;
import com.donation.carebridge.donation.domain.payment.application.in.PaymentConfirmer;
import com.donation.carebridge.donation.domain.payment.application.in.PaymentInitiator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentInitiator paymentInitiator;
    private final PaymentConfirmer paymentConfirmer;

    @PostMapping
    public ResponseEntity<ResponseMessage<PaymentResponse>> createPayment(
            @RequestBody CreatePaymentRequest request) {
        CreatePaymentResult createPaymentResult = paymentInitiator.initiate(request);
        PaymentResponse paymentResponse = PaymentResponse.from(createPaymentResult);
        return ResponseEntity.ok(ResponseMessage.success(paymentResponse));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ResponseMessage<PaymentResponse>> confirmPayment(
            @PathVariable String id,
            @RequestBody ConfirmPaymentRequest request) {
        ConfirmPaymentRequest confirmRequest = new ConfirmPaymentRequest(
                id, 
                request.idempotencyKey(), 
                request.payload()
        );
        ConfirmPaymentResult confirmResult = paymentConfirmer.confirm(confirmRequest);
        PaymentResponse paymentResponse = PaymentResponse.from(confirmResult);
        return ResponseEntity.ok(ResponseMessage.success(paymentResponse));
    }
}
