package com.example.algoproject.errors.exception.badrequest;

public class BadRequestException extends RuntimeException {
    public static final int statusCode = 400;
    public BadRequestException(String MESSAGE) {
        super(MESSAGE);
    }
}