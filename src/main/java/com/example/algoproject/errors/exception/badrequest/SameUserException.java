package com.example.algoproject.errors.exception.badrequest;

public class SameUserException extends RuntimeException {
    private static final String MESSAGE = "본인은 초대 또는 추방할 수 없습니다";
    public SameUserException() {
        super(MESSAGE);
    }
}
