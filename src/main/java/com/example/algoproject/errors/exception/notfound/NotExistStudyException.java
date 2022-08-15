package com.example.algoproject.errors.exception.notfound;

public class NotExistStudyException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 스터디입니다.";
    public NotExistStudyException() {
        super(MESSAGE);
    }
}
