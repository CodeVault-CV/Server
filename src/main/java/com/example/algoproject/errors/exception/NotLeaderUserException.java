package com.example.algoproject.errors.exception;

public class NotLeaderUserException extends RuntimeException {
    private static final String MESSAGE = "스터디는 리더만 삭제할 수 있습니다.";
    public NotLeaderUserException() {
        super(MESSAGE);
    }
}
