package com.example.deal.exception;

import feign.FeignException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class DealApiExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorMessage> handleException(FeignException exception) {
        return ResponseEntity
                .status(HttpStatus.valueOf(500))
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(SesCodeException.class)
    public ResponseEntity<ErrorMessage> handleException(SesCodeException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessage> handleException(NoSuchElementException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
