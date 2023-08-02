package com.example.conveyor.service;

import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.ScoringDataDTO;

import java.util.List;

public interface ScoringService {
    List<LoanOfferDTO> composeLoanOfferList(LoanApplicationRequestDTO loanApplicationRequestDTO);

    CreditDTO composeCreditDTO(ScoringDataDTO scoringDataDTO);
}
