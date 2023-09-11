package com.example.gateway.service.impl;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.gateway.service.GatewayService;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GatewayServiceImpl implements GatewayService {
    @Override
    public List<LoanOfferDTO> createLoanApplication(LoanApplicationRequestDTO dto) {
        return new ArrayList<>();
    }

    @Override
    public void chooseOneOffer(LoanOfferDTO dto) {

    }

    @Override
    public void finishRegistration(FinishRegistrationRequestDTO dto) {

    }

    @Override
    public void createDocumentRequest(Long id) {

    }

    @Override
    public void signDocumentRequest(Long id) {

    }

    @Override
    public void verifySesCodeRequest(Integer sesCode, Long id) {

    }
}
