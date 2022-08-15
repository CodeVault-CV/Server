package com.example.algoproject.errors.exception.unauthorized;

public class UnauthorizedException extends RuntimeException {
    public static final int statusCode = 401;
    public UnauthorizedException(String MESSAGE) {
        super(MESSAGE);
    }
}
