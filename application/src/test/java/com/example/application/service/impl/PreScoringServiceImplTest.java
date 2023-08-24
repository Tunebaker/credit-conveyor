package com.example.application.service.impl;

import com.example.application.exception.PreScoringException;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.service.PreScoringService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreScoringServiceImplTest {

    PreScoringService preScoringService = new PreScoringServiceImpl();

    @Test
    void preScore() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO()
                .birthdate(LocalDate.parse("2000-02-02"))
                .amount(BigDecimal.valueOf(10000))
                .email("qwe@rty.ru")
                .firstName("wefwef")
                .lastName("rtnr")
                .middleName("")
                .passportNumber("123456")
                .passportSeries("1234")
                .term(6);
        assertDoesNotThrow(() -> preScoringService.preScore(requestDTO));
    }

    @Test
    void preScoreWithBadParameters() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO()
                .birthdate(LocalDate.parse("2023-02-02"))
                .amount(BigDecimal.valueOf(100))
                .email("qwerty.ru")
                .firstName("w")
                .lastName("r")
                .middleName("3")
                .passportNumber("2123456")
                .passportSeries("12 34")
                .term(1);
        PreScoringException preScoringException = assertThrows(PreScoringException.class, () -> preScoringService.preScore(requestDTO));
        assertTrue(preScoringException.getMessage().contains("Серия паспорта=должна содержать 4 цифры без пробела"));
        assertTrue(preScoringException.getMessage().contains("Имя=должно содержать от 2 до 30 латинских букв"));
        assertTrue(preScoringException.getMessage().contains("Номер паспорта=должен содержать 6 цифр"));
        assertTrue(preScoringException.getMessage().contains("Фамилия=должна содержать от 2 до 30 латинских букв"));
        assertTrue(preScoringException.getMessage().contains("Сумма кредита=должна быть действительным числом, большим или равным 10000"));
        assertTrue(preScoringException.getMessage().contains("Срок кредита=должен быть целым числом, большим или равным 6"));
        assertTrue(preScoringException.getMessage().contains("Отчество=при наличии должно содержать от 2 до 30 латинских букв"));
        assertTrue(preScoringException.getMessage().contains("Email адрес=строка, подходящая под паттерн [\\w\\.]{2,50}@[\\w\\.]{2,20}"));
        assertTrue(preScoringException.getMessage().contains("Дата рождения=должна быть в числовом формате гггг-мм-дд, не позднее чем за 18 лет до текущего дня"));

    }
}