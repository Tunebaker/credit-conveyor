package com.example.conveyor.service;

import com.example.conveyor.dto.EmploymentDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.conveyor.dto.EmploymentDTO.EmploymentStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    Validator validator = new Validator();

    @Test
    void isRequestValid() {
        LoanApplicationRequestDTO requestDTO = LoanApplicationRequestDTO.builder()
                .birthdate(LocalDate.parse("2000-02-02"))
                .amount(BigDecimal.valueOf(10000))
                .email("qwe@rty.ru")
                .firstName("wefwef")
                .lastName("rtnr")
                .middleName("")
                .passportNumber("123456")
                .passportSeries("1234")
                .term(6)
                .build();
        boolean requestValid = validator.isRequestValid(requestDTO);
        assertTrue(requestValid);

    }

    @Test
    void isScoringDataValid() {
        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .birthdate(LocalDate.parse("2000-02-02"))
                .amount(BigDecimal.valueOf(120000))
                .employment(EmploymentDTO.builder()
                        .employmentStatus(EMPLOYED)
                        .workExperienceCurrent(3)
                        .workExperienceTotal(12)
                        .salary(new BigDecimal(10000))
                        .build())
                .build();
        boolean scoringDataValid = validator.isScoringDataValid(scoringDataDTO);
        assertTrue(scoringDataValid);

    }
}