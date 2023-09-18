package com.example.gateway.service.impl;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.gateway.service.FeignApplicationService;
import com.example.gateway.service.FeignDealService;
import com.example.gateway.service.GatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock
    FeignApplicationService feignApplicationService;
    @Mock
    FeignDealService feignDealService;
    GatewayService service;
    LoanApplicationRequestDTO loanApplicationRequestDTO;
    LoanOfferDTO loanOfferDTO;
    FinishRegistrationRequestDTO finishRegistrationRequestDTO;
    Long id;

    @BeforeEach
    void init() {
        service = new GatewayServiceImpl(feignApplicationService, feignDealService);
        id = 17L;

    }

    @Test
    void createLoanApplication() {
        loanApplicationRequestDTO = LoanApplicationRequestDTO.builder().build();

        service.createLoanApplication(loanApplicationRequestDTO);

        verify(feignApplicationService, times(1)).createApplication(loanApplicationRequestDTO);
    }

    @Test
    void chooseOneOffer() {
        loanOfferDTO = LoanOfferDTO.builder().build();

        service.chooseOneOffer(loanOfferDTO);

        verify(feignApplicationService, times(1)).applyOffer(loanOfferDTO);
    }

    @Test
    void finishRegistration() {
        finishRegistrationRequestDTO = FinishRegistrationRequestDTO.builder().build();

        service.finishRegistration(id, finishRegistrationRequestDTO);

        verify(feignDealService, times(1)).calculateCredit(id, finishRegistrationRequestDTO);
    }

    @Test
    void createDocumentRequest() {
        service.createDocumentRequest(id);

        verify(feignDealService, times(1)).sendDocuments(id);
    }

    @Test
    void signDocumentRequest() {
        service.signDocumentRequest(id);

        verify(feignDealService, times(1)).signDocuments(id);
    }

    @Test
    void verifySesCodeRequest() {
        service.verifySesCodeRequest(id, 2345);

        verify(feignDealService, times(1)).verifySesCode(id, 2345);
    }
}