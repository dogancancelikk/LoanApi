package com.example.creditmodule.exception;

import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

    private final ErrorCode messageCode;
    private final String[] params;

    public GenericException(ErrorCode messageCode, String... params) {
        this.messageCode = messageCode;
        this.params = params;
    }

    public GenericException(ErrorCode messageCode, Throwable cause, String... params) {
        super(cause);
        this.messageCode = messageCode;
        this.params = params;
    }
}
