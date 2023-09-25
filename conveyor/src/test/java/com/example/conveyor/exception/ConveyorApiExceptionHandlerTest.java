package com.example.conveyor.exception;

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
class ConveyorApiExceptionHandlerTest {

    @InjectMocks
    private ConveyorApiExceptionHandler handler;

    @Test
    void handlePreScoringException() {
        ResponseEntity<ErrorMessage> error = handler.handleException(new ScoringException("ошибка"));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertTrue(Objects.requireNonNull(error.getBody()).getMessage().contains("ошибка"));
    }

}