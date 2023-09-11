package com.example.gateway.service;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;

import java.util.List;

public interface GatewayService {
    List<LoanOfferDTO> createLoanApplication(LoanApplicationRequestDTO dto);

    void chooseOneOffer(LoanOfferDTO dto);

    void finishRegistration(FinishRegistrationRequestDTO dto);

    void createDocumentRequest(Long id);

    void signDocumentRequest(Long id);

    void verifySesCodeRequest(Integer sesCode, Long id);
}
