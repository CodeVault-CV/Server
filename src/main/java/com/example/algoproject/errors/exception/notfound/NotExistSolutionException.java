package com.example.algoproject.errors.exception.notfound;

public class NotExistSolutionException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 솔루션입니다.";
    public NotExistSolutionException() {
        super(MESSAGE);
    }
}
