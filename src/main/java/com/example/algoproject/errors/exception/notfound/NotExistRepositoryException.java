package com.example.algoproject.errors.exception.notfound;

public class NotExistRepositoryException extends RuntimeException {
    private static final String MESSAGE = "존재하지 않는 저장소입니다.";
    public NotExistRepositoryException() {
        super(MESSAGE);
    }
}
