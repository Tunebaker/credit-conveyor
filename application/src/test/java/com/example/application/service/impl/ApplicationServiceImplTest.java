package com.example.application.service.impl;

import com.example.application.exception.PreScoringException;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.application.service.client.FeignDealService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    FeignDealService feignDealService;
    @Mock
    PreScoringServiceImpl preScoringService;
    @InjectMocks
    ApplicationServiceImpl applicationService;

    @Test
    void createApplication() {
        LoanApplicationRequestDTO request = new LoanApplicationRequestDTO()
                .amount(BigDecimal.ONE)
                .birthdate(LocalDate.of(2000, 12, 30))
                .email("qwe@rty.ru")
                .firstName("Vasia")
                .lastName("Vasin")
                .middleName("Vasilievich")
                .passportNumber("3333")
                .passportSeries("333333")
                .term(6);
        when(preScoringService.preScore(request))
                .thenReturn(new HashMap<>(0));
        when(feignDealService.createApplication(request)).thenReturn(new ArrayList<>());

        List<LoanOfferDTO> loanOfferDTOs = applicationService.createApplication(request);

        assertEquals(0, loanOfferDTOs.size());
    }

    @Test
    void createApplicationInvalidRequestParams() {
        LoanApplicationRequestDTO request = LoanApplicationRequestDTO.builder().build();
        Map<String, String> validationErrors = Map.of("имя", "должно быть не менее 2 символов");
        when(preScoringService.preScore(any(LoanApplicationRequestDTO.class)))
                .thenReturn(validationErrors);
        PreScoringException preScoringException = assertThrows(PreScoringException.class, () -> applicationService.createApplication(request));
        assertTrue(preScoringException.getMessage().contains("заявка не прошла прескоринг по причинам: "));
    }

    @Test
    void applyOffer() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .applicationId(1L)
                .term(45)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .monthlyPayment(BigDecimal.TEN)
                .rate(BigDecimal.ONE)
                .requestedAmount(BigDecimal.TEN)
                .totalAmount(BigDecimal.TEN);

        applicationService.applyOffer(loanOfferDTO);

        ArgumentCaptor<LoanOfferDTO> offerDTOArgumentCaptor = ArgumentCaptor.forClass(LoanOfferDTO.class);

        verify(feignDealService, times(1)).applyOffer(offerDTOArgumentCaptor.capture());
        LoanOfferDTO capturedOffer = offerDTOArgumentCaptor.getValue();
        assertEquals(1, capturedOffer.getApplicationId());
        assertEquals(45, capturedOffer.getTerm());
        assertEquals(true, capturedOffer.getIsInsuranceEnabled());
        assertEquals(true, capturedOffer.getIsSalaryClient());
        assertEquals(BigDecimal.TEN, capturedOffer.getMonthlyPayment());
        assertEquals(BigDecimal.ONE, capturedOffer.getRate());
        assertEquals(BigDecimal.TEN, capturedOffer.getRequestedAmount());
        assertEquals(BigDecimal.TEN, capturedOffer.getTotalAmount());
        assertEquals(capturedOffer, loanOfferDTO);

        verifyNoInteractions(preScoringService);

    }
}