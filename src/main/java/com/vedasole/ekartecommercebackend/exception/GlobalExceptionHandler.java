package com.vedasole.ekartecommercebackend.exception;

import com.vedasole.ekartecommercebackend.payload.ApiResponse;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(
            ResourceNotFoundException ex
    ){
        String message = ex.getMessage();
        log.error("ResourceNotFoundException: {}", message, ex);

        ApiResponse apiResponse = new ApiResponse(message, false);

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException ex
    ){
        HashMap<String, String> errorList = new HashMap<>();

        ex.getAllErrors().forEach(e -> {
            String objectName = ((FieldError)e).getField();
            String message = e.getDefaultMessage();
            errorList.put(objectName, message);
        });

        log.error("MethodArgumentNotValidException: {}", errorList, ex);

        return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ApiResponse> apiExceptionHandler(APIException ex) {

        String message = ex.getMessage();
        HttpStatus status = ex.getHttpStatus();
        ApiResponse apiResponse = new ApiResponse(message, false);

        log.error("APIException: {}", message, ex);

        return new ResponseEntity<>(
                apiResponse,
                status
        );
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse> badCredentialsOrIllegalArgumentExceptionHandler(RuntimeException ex) {

        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);

        log.error("{} : {}", ex.getClass().getSimpleName(), message, ex);

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.BAD_REQUEST
        );

    }

    @ExceptionHandler({
            JwtException.class,
            MalformedJwtException.class,
            ExpiredJwtException.class,
            ClaimJwtException.class,
            UnsupportedJwtException.class,
            PrematureJwtException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ApiResponse> authenticationExceptionHandler(Exception ex) {
        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

        log.error("{}: {}", ex.getClass().getSimpleName(), message, ex);

        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> globalExceptionHandler(Exception ex) {
        String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

        log.error("{}: {}", ex.getClass().getSimpleName(), message, ex);

        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}