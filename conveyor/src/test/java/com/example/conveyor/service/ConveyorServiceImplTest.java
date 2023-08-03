package com.example.conveyor.service;

import com.example.conveyor.model.*;
import com.example.conveyor.service.impl.ConveyorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.EMPLOYED;
import static com.example.conveyor.model.EmploymentDTO.PositionEnum.TOP_MANAGER;
import static com.example.conveyor.model.ScoringDataDTO.GenderEnum.MALE;
import static com.example.conveyor.model.ScoringDataDTO.MaritalStatusEnum.SINGLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConveyorServiceImplTest {
    @Mock
    private Validator validator;

    @InjectMocks
    private ConveyorServiceImpl conveyorServiceImpl;

    @Test
    void composeLoanOfferList() {
        LoanApplicationRequestDTO requestDTO = LoanApplicationRequestDTO.builder()
                .term(6)
                .passportSeries("123456")
                .passportNumber("1234")
                .firstName("qwe")
                .middleName("")
                .lastName("rty")
                .email("qwe@rty")
                .amount(new BigDecimal(10000))
                .birthdate(LocalDate.parse("2000-01-01"))
                .build();
        when(validator.preScore(any(LoanApplicationRequestDTO.class))).thenReturn("");
        List<LoanOfferDTO> loanOfferDTOs = conveyorServiceImpl.composeLoanOfferList(requestDTO);
        assertEquals(4, loanOfferDTOs.size());
        assertEquals(10000, loanOfferDTOs.get(0).getTotalAmount().intValue());
    }

    @Test
    void composeCreditDTO() {
        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .birthdate(LocalDate.parse("2000-02-02"))
                .amount(BigDecimal.valueOf(120000))
                .employment(EmploymentDTO.builder()
                        .employmentStatus(EMPLOYED)
                        .workExperienceCurrent(3)
                        .workExperienceTotal(12)
                        .salary(new BigDecimal(10000))
                        .position(TOP_MANAGER)
                        .build())
                .isSalaryClient(true)
                .isInsuranceEnabled(true)
                .gender(MALE)
                .dependentAmount(1)
                .term(12)
                .maritalStatus(SINGLE)
                .build();
        when(validator.score(any(ScoringDataDTO.class))).thenReturn("");
        CreditDTO creditDTO = conveyorServiceImpl.composeCreditDTO(scoringDataDTO);
        assertEquals(12, creditDTO.getPaymentSchedule().size());
        assertTrue(creditDTO.getIsInsuranceEnabled());
    }
}