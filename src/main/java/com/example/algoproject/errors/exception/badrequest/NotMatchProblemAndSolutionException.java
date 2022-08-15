package com.example.algoproject.errors.exception.badrequest;

public class NotMatchProblemAndSolutionException extends BadRequestException {
    private static final String MESSAGE = "해당 문제에 속하지 않는 솔루션입니다.";
    public NotMatchProblemAndSolutionException() {
        super(MESSAGE);
    }
}