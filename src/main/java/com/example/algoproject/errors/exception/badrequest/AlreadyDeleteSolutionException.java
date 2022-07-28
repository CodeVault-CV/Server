package com.example.algoproject.errors.exception.badrequest;

public class AlreadyDeleteSolutionException extends RuntimeException{
    private static final String MESSAGE = "삭제하려는 파일이 Github에 존재하지 않습니다.";
    public AlreadyDeleteSolutionException() {
        super(MESSAGE);
    }
}
