package com.vedasole.ekartecommercebackend.exception;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class APIException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(APIException.class);
    public String message;
    public HttpStatus httpStatus;

    public APIException() {
        this.message = "Error Response";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public APIException(String message) {
        super(message);
        this.message = message;
    }

    public APIException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public APIException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
