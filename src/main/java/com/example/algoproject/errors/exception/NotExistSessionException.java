package com.example.algoproject.errors.exception;

public class NotExistSessionException extends RuntimeException{
    private static final String MESSAGE = "존재하지 않는 세션입니다.";
    public NotExistSessionException() {
        super(MESSAGE);
    }
}
