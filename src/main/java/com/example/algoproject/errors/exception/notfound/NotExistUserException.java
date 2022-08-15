package com.example.algoproject.errors.exception.notfound;

public class NotExistUserException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 유저입니다.";
    public NotExistUserException() { super(MESSAGE); }
}
