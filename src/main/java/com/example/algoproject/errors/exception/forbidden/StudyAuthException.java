package com.example.algoproject.errors.exception.forbidden;

public class StudyAuthException extends RuntimeException {
    private static final String MESSAGE = "해당 스터디에 접근 권한이 없습니다";
    public StudyAuthException() {
        super(MESSAGE);
    }
}
