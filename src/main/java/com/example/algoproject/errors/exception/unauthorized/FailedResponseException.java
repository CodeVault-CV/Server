package com.example.algoproject.errors.exception.unauthorized;

public class FailedResponseException extends UnauthorizedException {
    public FailedResponseException(String MESSAGE) {
        super(MESSAGE);
    }
}
