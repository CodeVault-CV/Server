package com.example.algoproject.errors.exception.notfound;

public class NotExistTagException extends NotFoundException{
    private static final String MESSAGE = "존재하지 않는 태그입니다";
    public NotExistTagException() {
        super(MESSAGE);
    }
}
