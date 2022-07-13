package com.example.algoproject.errors.exception;

public class NotExistSolutionException extends RuntimeException{
    private static final String MESSAGE = "존재하지 않는 솔루션입니다.";
    public NotExistSolutionException() {
        super(MESSAGE);
    }
}
