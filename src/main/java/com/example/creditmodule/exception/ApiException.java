package com.example.creditmodule.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    @Getter
    private final HttpStatus status;
    private final String message;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
    @Override
    public String getMessage() {
        return message;
    }

}
