package com.example.conveyor.service;

import com.example.conveyor.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.conveyor.dto.EmploymentDTO.EmploymentStatus.EMPLOYED;
import static com.example.conveyor.dto.EmploymentDTO.Position.TOP_MANAGER;
import static com.example.conveyor.dto.ScoringDataDTO.Gender.MALE;
import static com.example.conveyor.dto.ScoringDataDTO.MaritalStatus.SINGLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConveyorServiceTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private ConveyorService conveyorService;

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
        when(validator.isRequestValid(any(LoanApplicationRequestDTO.class))).thenReturn(true);
        List<LoanOfferDTO> loanOfferDTOs = conveyorService.composeLoanOfferList(requestDTO);
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
        when(validator.isScoringDataValid(any(ScoringDataDTO.class))).thenReturn(true);
        CreditDTO creditDTO = conveyorService.composeCreditDTO(scoringDataDTO);
        assertEquals(12, creditDTO.getPaymentSchedule().size());
        assertTrue(creditDTO.getIsInsuranceEnabled());
    }
}