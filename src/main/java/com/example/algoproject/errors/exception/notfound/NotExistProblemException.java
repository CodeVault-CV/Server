package com.example.algoproject.errors.exception.notfound;

public class NotExistProblemException extends RuntimeException{
    private static final String MESSAGE = "존재하지 않는 문제입니다.";
    public NotExistProblemException() {
        super(MESSAGE);
    }
}
