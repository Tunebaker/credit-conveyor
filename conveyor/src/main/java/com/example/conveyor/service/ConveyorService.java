package com.example.conveyor.service;

import com.example.conveyor.model.CreditDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.LoanOfferDTO;
import com.example.conveyor.model.ScoringDataDTO;

import java.util.List;

public interface ConveyorService {
    List<LoanOfferDTO> composeLoanOfferList(LoanApplicationRequestDTO loanApplicationRequestDTO);

    CreditDTO composeCreditDTO(ScoringDataDTO scoringDataDTO);
}
