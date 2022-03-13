package com.example.algoproject.errors.exception;

public class NotExistStudyException extends RuntimeException{
    private static final String MESSAGE = "존재하지 않는 스터디입니다.";
    public NotExistStudyException() {
        super(MESSAGE);
    }
}
