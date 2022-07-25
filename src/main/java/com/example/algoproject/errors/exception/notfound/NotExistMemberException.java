package com.example.algoproject.errors.exception.notfound;

public class NotExistMemberException extends RuntimeException {
    private static final String MESSAGE = "존재하지 멤버입니다.";
    public NotExistMemberException() {
        super(MESSAGE);
    }
}
