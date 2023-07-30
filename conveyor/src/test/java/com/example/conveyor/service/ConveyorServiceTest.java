package com.example.conveyor.service;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    }
}