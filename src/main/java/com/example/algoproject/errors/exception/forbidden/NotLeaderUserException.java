package com.example.algoproject.errors.exception.forbidden;

public class NotLeaderUserException extends RuntimeException {
    private static final String MESSAGE = "팀장(리더)이 아닙니다";
    public NotLeaderUserException() {
        super(MESSAGE);
    }
}
