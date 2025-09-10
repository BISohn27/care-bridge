package com.donation.carebridge.payment.dto;

public record ResponseMessage<T>(
        T data,
        String message,
        String errorCode
) {
    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(data, null, null);
    }

    public static <T> ResponseMessage<T> success(T data, String message) {
        return new ResponseMessage<>(data, message, null);
    }

    public static <T> ResponseMessage<T> error(String errorCode, String message) {
        return new ResponseMessage<>(null, message, errorCode);
    }

    public static <T> ResponseMessage<T> error(String message) {
        return new ResponseMessage<>(null, message, null);
    }
}
