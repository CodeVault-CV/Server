package com.example.algoproject.errors.exception.badrequest;

public class AlreadyExistRepositoryNameException extends BadRequestException {
    private static final String MESSAGE = "같은 이름의 Repository가 이미 존재합니다";
    public AlreadyExistRepositoryNameException() {
        super(MESSAGE);
    }
}
