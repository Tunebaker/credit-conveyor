package com.example.application.controller;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.application.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {
    @Mock
    private ApplicationServiceImpl applicationService;
    @InjectMocks
    private ApplicationController applicationController;

    @Test
    void createApplication() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO();
        dto.setAmount(BigDecimal.ONE);
        dto.setBirthdate(LocalDate.of(2000, 12, 13));
        dto.setAmount(BigDecimal.TEN);
        dto.setEmail("sdf@dfb");
        dto.setTerm(7);
        dto.setFirstName("Vasia");
        dto.setLastName("Vasia");
        dto.setMiddleName("Vasia");
        dto.setPassportNumber("444444");
        dto.setPassportSeries("4444");

        List<LoanOfferDTO> loanOfferDTOs = List.of(LoanOfferDTO.builder().build());
        when(applicationService.createApplication(dto)).thenReturn(loanOfferDTOs);

        ResponseEntity<List<LoanOfferDTO>> responseEntity = applicationController.createApplication(dto);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void applyOffer() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(1L);
        loanOfferDTO.setIsInsuranceEnabled(true);
        loanOfferDTO.setIsSalaryClient(true);
        loanOfferDTO.setRate(BigDecimal.ONE);
        loanOfferDTO.setMonthlyPayment(BigDecimal.ONE);
        loanOfferDTO.setRequestedAmount(BigDecimal.ONE);
        loanOfferDTO.setTotalAmount(BigDecimal.ONE);
        loanOfferDTO.setTerm(6);

        ResponseEntity<Void> responseEntity = applicationController.applyOffer(loanOfferDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


    }
}