package com.example.creditmodule.exception;

import lombok.Getter;

@Getter
public enum GenericErrorCode implements ErrorCode {

    UNKNOWN(0),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    private final int code;

    GenericErrorCode(int code) {
        this.code = code;
    }

}
