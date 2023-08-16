package com.example.deal.mapper;

import com.example.deal.model.CreditDTO;
import com.example.deal.model.CreditEntity;
import com.example.deal.model.PaymentScheduleElement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.deal.model.CreditStatus.CALCULATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditMapperTest {

    @Test
    void creditDTOToCreditTest() {

        PaymentScheduleElement build = new PaymentScheduleElement()
                .date(LocalDate.now())
                .debtPayment(BigDecimal.TEN)
                .interestPayment(BigDecimal.TEN)
                .number(2)
                .remainingDebt(BigDecimal.TEN)
                .totalPayment(BigDecimal.TEN);

        CreditDTO creditDTO = new CreditDTO()
                .term(20)
                .amount(BigDecimal.ONE)
                .monthlyPayment(BigDecimal.ONE)
                .rate(BigDecimal.ONE)
                .psk(BigDecimal.ONE)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .paymentSchedule(List.of(build));

        CreditEntity credit = CreditMapper.INSTANCE.creditDTOToCredit(creditDTO);
        PaymentScheduleElement scheduleElement = credit.getPaymentSchedule().get(0);
        assertEquals(20, credit.getTerm());
        assertTrue(credit.getInsuranceEnable());
        assertFalse(credit.getSalaryClient());
        assertEquals(1, credit.getPaymentSchedule().size());
        assertEquals(BigDecimal.TEN, scheduleElement.getDebtPayment());
        assertEquals(BigDecimal.TEN, scheduleElement.getInterestPayment());
        assertEquals(BigDecimal.TEN, scheduleElement.getRemainingDebt());
        assertEquals(BigDecimal.TEN, scheduleElement.getTotalPayment());
        assertEquals(CALCULATED, credit.getCreditStatus());
        assertEquals(BigDecimal.ONE, credit.getAmount());
        assertEquals(BigDecimal.ONE, credit.getRate());
        assertEquals(BigDecimal.ONE, credit.getPsk());
        assertEquals(BigDecimal.ONE, credit.getMonthlyPayment());
        assertEquals(scheduleElement.toString(), build.toString());


        CreditEntity credit2 = CreditMapper.INSTANCE.creditDTOToCredit(null);
        assertNull(credit2);

    }
}