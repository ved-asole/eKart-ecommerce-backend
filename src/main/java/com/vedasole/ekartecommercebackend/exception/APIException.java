package com.vedasole.ekartecommercebackend.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Slf4j
public class APIException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    public APIException(String message) {
        super(message);
        this.message = message;
        this.httpStatus= HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public APIException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.httpStatus= HttpStatus.INTERNAL_SERVER_ERROR;
    }

}