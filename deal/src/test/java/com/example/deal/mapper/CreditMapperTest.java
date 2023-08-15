package com.example.deal.mapper;

import com.example.deal.model.CreditDTO;
import com.example.deal.model.CreditEntity;
import org.junit.jupiter.api.Test;

import static com.example.deal.model.CreditStatus.CALCULATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditMapperTest {

    @Test
    void creditDTOToCreditTest() {
        CreditDTO creditDTO = CreditDTO.builder()
                .term(20)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        CreditEntity credit = CreditMapper.INSTANCE.creditDTOToCredit(creditDTO);
        assertEquals(20, credit.getTerm());
        assertTrue(credit.getInsuranceEnable());
        assertFalse(credit.getSalaryClient());
        assertEquals(CALCULATED, credit.getCreditStatus());

        CreditEntity credit2 = CreditMapper.INSTANCE.creditDTOToCredit(null);
        assertNull(credit2);
    }
}