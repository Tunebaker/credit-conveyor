package com.example.deal.service;

import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DealService {
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);
    void applyOffer(LoanOfferDTO loanOfferDTO);
    void calculateCredit(FinishRegistrationRequestDTO finishRegistrationRequestDTO);
}
