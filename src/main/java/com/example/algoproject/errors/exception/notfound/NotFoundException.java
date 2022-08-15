package com.example.algoproject.errors.exception.notfound;

public class NotFoundException extends RuntimeException {
    public static final int statusCode = 404;
    public NotFoundException(String MESSAGE) {
        super(MESSAGE);
    }
}
