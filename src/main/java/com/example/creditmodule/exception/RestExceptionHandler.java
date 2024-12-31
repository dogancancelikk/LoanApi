package com.example.creditmodule.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleBusinessException(ApiException exception) {
        ApiError apiError = ApiError
                .builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .timestamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Serializable> handleConstraintViolationException(MethodArgumentNotValidException exception) {
        log.error("Method Argument Not Valid Exception: ", exception);

        String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElseThrow(() -> new ApiException("", HttpStatus.BAD_REQUEST));

        ApiError apiError = ApiError
                .builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(ZonedDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}

