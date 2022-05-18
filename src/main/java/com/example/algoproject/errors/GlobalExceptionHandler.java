package com.example.algoproject.errors;

import com.example.algoproject.errors.exception.*;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseService responseService;

    @ExceptionHandler({NotExistUserException.class, NotExistStudyException.class, NotExistProblemException.class,
            NotExistSolutionException.class})
    CommonResponse handleBadRequestException(Exception ex) {
        return handleBadRequest(ex);
    }

    @ExceptionHandler(Exception.class)
    CommonResponse handleException(Exception ex) {
        return handleInternalServerError(ex);
    }

    private CommonResponse handleBadRequest (Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    private CommonResponse handleInternalServerError (Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}
