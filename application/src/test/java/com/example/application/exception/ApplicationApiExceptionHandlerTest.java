package com.example.application.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationApiExceptionHandlerTest {
    @InjectMocks
    private ApplicationApiExceptionHandler handler;

    @Test
    void handlePreScoringException() {
        ResponseEntity<ErrorMessage> error = handler.handlePreScoringException(new PreScoringException("ошибка валидации"));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertTrue(Objects.requireNonNull(error.getBody()).getMessage().contains("ошибка"));
    }

    @Test
    void handleJsonParseException() {
        ResponseEntity<ErrorMessage> error = handler.handleJsonParseException(new InvalidFormatException(" ", new Object(), Integer.class));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertTrue(Objects.requireNonNull(error.getBody()).getMessage().contains("Ошибка в данных запроса:"));

    }
}