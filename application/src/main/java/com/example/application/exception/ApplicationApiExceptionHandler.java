package com.example.application.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationApiExceptionHandler {
    @ExceptionHandler(PreScoringException.class)
    public ResponseEntity<ErrorMessage> handlePreScoringException(PreScoringException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler({InvalidFormatException.class, JsonParseException.class})
    public ResponseEntity<ErrorMessage> handleJsonParseException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("Ошибка в данных запроса: " + exception.getMessage()));
    }
}
