package com.example.deal.service;

import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);

    void applyOffer(LoanOfferDTO loanOfferDTO);

    void calculateCredit(FinishRegistrationRequestDTO finishRegistrationRequestDTO, Long applicationId);
}
