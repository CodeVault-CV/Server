package com.example.algoproject.errors.exception;

public class FailedResponseException extends RuntimeException {
    public FailedResponseException(String MESSAGE) {
        super(MESSAGE);
    }
}
