package com.example.algoproject.errors.exception;

public class AlreadyExistMemberException extends RuntimeException{
    private static final String MESSAGE = "이미 추가된 멤버 입니다";
    public AlreadyExistMemberException() {
        super(MESSAGE);
    }
}
