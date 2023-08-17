package com.example.deal.exception;

import feign.FeignException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DealApiExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorMessage> handleException(FeignException exception) {
        return ResponseEntity
                .status(HttpStatus.valueOf(500))
                .body(new ErrorMessage(exception.getMessage()));
    }
}
