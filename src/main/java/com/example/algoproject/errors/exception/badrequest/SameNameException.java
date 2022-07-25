package com.example.algoproject.errors.exception.badrequest;

public class SameNameException extends RuntimeException {
    private static final String MESSAGE = "기존의 이름과 같습니다";
    public SameNameException() {
        super(MESSAGE);
    }
}
