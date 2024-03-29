package com.example.algoproject.errors.exception.badrequest;

public class AlreadyExistSolutionException extends BadRequestException {
    private static final String MESSAGE = "이미 해당 문제에 솔루션이 존재합니다.";
    public AlreadyExistSolutionException() {
        super(MESSAGE);
    }
}
