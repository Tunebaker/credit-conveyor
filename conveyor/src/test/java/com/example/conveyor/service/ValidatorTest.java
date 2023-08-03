package com.example.conveyor.service;

import com.example.conveyor.model.EmploymentDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.ScoringDataDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.EMPLOYED;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidatorTest {
    Validator validator = new Validator();

    @Test
    void preScore() {
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
        String requestValid = validator.preScore(requestDTO);
        assertEquals("", requestValid);

    }

    @Test
    void score() {
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
        String scoringDataValid = validator.score(scoringDataDTO);
        assertEquals("", scoringDataValid);

    }
}