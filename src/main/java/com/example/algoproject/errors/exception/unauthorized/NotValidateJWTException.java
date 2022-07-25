package com.example.algoproject.errors.exception.unauthorized;

public class NotValidateJWTException extends RuntimeException {
    private static final String MESSAGE = "유효하지 않은 토큰입니다.";
    public NotValidateJWTException() {
        super(MESSAGE);
    }
}
