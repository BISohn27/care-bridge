package com.donation.carebridge.common.domain.idempotency.exception;

import lombok.Getter;

@Getter
public class IdempotencyException extends RuntimeException {

    private final String errorCode;

    public IdempotencyException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
