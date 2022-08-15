package com.example.algoproject.errors.exception.forbidden;

public class ForbiddenException extends RuntimeException {
    public static final int statusCode = 403;
    public ForbiddenException(String MESSAGE) {
        super(MESSAGE);
    }
}
