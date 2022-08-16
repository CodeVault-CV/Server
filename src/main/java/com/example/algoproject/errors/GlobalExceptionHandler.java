package com.example.algoproject.errors;

import com.example.algoproject.errors.exception.badrequest.*;
import com.example.algoproject.errors.exception.forbidden.ForbiddenException;
import com.example.algoproject.errors.exception.notfound.*;
import com.example.algoproject.errors.exception.unauthorized.UnauthorizedException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseService responseService;

    @ExceptionHandler({BadRequestException.class})
    CommonResponse handleBadRequestException(Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(BadRequestException.statusCode, ex.getMessage());
    }

    @ExceptionHandler({UnauthorizedException.class})
    CommonResponse handleUnauthorizedException(Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(UnauthorizedException.statusCode, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    CommonResponse handleNotFoundException(Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(NotFoundException.statusCode, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.info(message);
        return responseService.getErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler({ForbiddenException.class})
    public CommonResponse handleForbiddenException(Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(ForbiddenException.statusCode, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    CommonResponse handleException(Exception ex) {
        log.info(ex.getMessage());
        return responseService.getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}
