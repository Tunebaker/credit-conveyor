package com.example.application.service;


import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;

import java.util.List;

public interface ApplicationService {

    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);

    void applyOffer(LoanOfferDTO loanOfferDTO);
}
