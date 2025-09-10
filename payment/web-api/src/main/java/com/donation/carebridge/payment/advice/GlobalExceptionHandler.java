package com.donation.carebridge.payment.advice;

import com.donation.carebridge.payment.dto.ResponseMessage;
import com.donation.carebridge.payments.payment.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ResponseMessage<Void>> handleGeneral(PaymentException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.badRequest().body(
                ResponseMessage.error(exception.getErrorCode(), "요청이 잘못되었습니다."));
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ResponseMessage<Void>> handleGeneral(Exception exception) {
        log.error("Unexpected error", exception);
        return ResponseEntity.internalServerError().body(
                ResponseMessage.error("INTERNAL_ERROR", "내부 오류가 발생했습니다"));
    }
}
