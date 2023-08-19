package com.example.application.service.impl;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.service.PreScoringService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        Map<String, String> requestValid = preScoringService.preScore(requestDTO);
        assertEquals(0, requestValid.size());
        assertFalse(requestValid.containsKey("Фамилия"));
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
        Map<String, String> requestValid = preScoringService.preScore(requestDTO);
        assertEquals(9, requestValid.size());
        assertTrue(requestValid.containsKey("Фамилия"));

    }
}