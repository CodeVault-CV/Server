package com.example.algoproject.errors;

import com.example.algoproject.errors.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotExistUserException.class)
    ResponseEntity<ErrorResponse> handleNotExistUser(NotExistUserException ex) {
        return handleBadRequest(ex);
    }

    @ExceptionHandler(NotExistStudyException.class)
    ResponseEntity<ErrorResponse> handleNotExistStudy(NotExistStudyException ex) {
        return handleBadRequest(ex);
    }

    @ExceptionHandler(NotExistProblemException.class)
    ResponseEntity<ErrorResponse> handleNotExistProblem(NotExistProblemException ex) {
        return handleBadRequest(ex);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity handleException(Exception ex) {
        return handleInternalServerError(ex);
    }

    private ResponseEntity<ErrorResponse> handleBadRequest (Exception ex) {
        final ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity handleInternalServerError (Exception ex) {
        final ErrorResponse response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
