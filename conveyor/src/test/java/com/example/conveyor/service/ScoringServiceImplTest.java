package com.example.conveyor.service;

import com.example.conveyor.model.EmploymentDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.ScoringDataDTO;
import com.example.conveyor.service.impl.ScoringServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.EMPLOYED;
import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED;
import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceImplTest {

    ScoringService scoringServiceImpl = new ScoringServiceImpl();

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
        Map<String, String> requestValid = scoringServiceImpl.preScore(requestDTO);
        assertEquals(0, requestValid.size());
        assertFalse(requestValid.containsKey("Фамилия"));
    }

    @Test
    void preScoreWithBadParameters() {
        LoanApplicationRequestDTO requestDTO = LoanApplicationRequestDTO.builder()
                .birthdate(LocalDate.parse("2023-02-02"))
                .amount(BigDecimal.valueOf(100))
                .email("qwerty.ru")
                .firstName("w")
                .lastName("r")
                .middleName("3")
                .passportNumber("2123456")
                .passportSeries("12 34")
                .term(1)
                .build();
        Map<String, String> requestValid = scoringServiceImpl.preScore(requestDTO);
        assertEquals(9, requestValid.size());
        assertTrue(requestValid.containsKey("Фамилия"));

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
        Map<String, String> scoringDataValid = scoringServiceImpl.score(scoringDataDTO);
        assertEquals(0, scoringDataValid.size());
    }

    @Test
    void scoreWithBadParameters(){
        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .birthdate(LocalDate.parse("2023-02-02"))
                .amount(BigDecimal.valueOf(120000))
                .employment(EmploymentDTO.builder()
                        .employmentStatus(UNEMPLOYED)
                        .workExperienceCurrent(2)
                        .workExperienceTotal(11)
                        .salary(new BigDecimal(1000))
                        .build())
                .build();
        Map<String, String> scoringDataValid = scoringServiceImpl.score(scoringDataDTO);
        assertEquals(5, scoringDataValid.size());
    }

    @Test
    void scoreWithTooOldClient(){
        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .birthdate(LocalDate.parse("1940-02-02"))
                .amount(BigDecimal.valueOf(120000))
                .employment(EmploymentDTO.builder()
                        .employmentStatus(EMPLOYED)
                        .workExperienceCurrent(21)
                        .workExperienceTotal(111)
                        .salary(new BigDecimal(10000))
                        .build())
                .build();
        Map<String, String> scoringDataValid = scoringServiceImpl.score(scoringDataDTO);
        assertEquals(1, scoringDataValid.size());
    }
}