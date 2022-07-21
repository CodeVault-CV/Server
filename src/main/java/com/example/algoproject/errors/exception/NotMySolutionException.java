package com.example.algoproject.errors.exception;

public class NotMySolutionException extends RuntimeException {
    private static final String MESSAGE = "자신이 등록한 솔루션만 삭제할 수 있습니다.";
    public NotMySolutionException() {
        super(MESSAGE);
    }
}
