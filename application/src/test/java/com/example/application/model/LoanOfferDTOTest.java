package com.example.application.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanOfferDTOTest {

    @Test
    void testEquals() {
        LoanOfferDTO dto1 = LoanOfferDTO.builder()
                .applicationId(1L)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .rate(BigDecimal.ONE)
                .monthlyPayment(BigDecimal.ONE)
                .requestedAmount(BigDecimal.ONE)
                .totalAmount(BigDecimal.ONE)
                .term(6)
                .build();
        LoanOfferDTO dto2 = new LoanOfferDTO()
                .applicationId(1L)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .rate(BigDecimal.ONE)
                .monthlyPayment(BigDecimal.ONE)
                .requestedAmount(BigDecimal.ONE)
                .totalAmount(BigDecimal.ONE)
                .term(6);
        assertEquals(dto2, dto1);
    }
}