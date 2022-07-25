package com.example.algoproject.errors.exception.unauthorized;

public class FailedResponseException extends RuntimeException {
    public FailedResponseException(String MESSAGE) {
        super(MESSAGE);
    }
}
