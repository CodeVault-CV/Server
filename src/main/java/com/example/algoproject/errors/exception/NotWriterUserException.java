package com.example.algoproject.errors.exception;

public class NotWriterUserException extends RuntimeException {
    private static final String MESSAGE = "해당 글의 글쓴이가 아닙니다";
    public NotWriterUserException() {
        super(MESSAGE);
    }
}
