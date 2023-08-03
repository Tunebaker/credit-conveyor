package com.example.conveyor.service;

import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.ScoringDataDTO;

import java.util.Map;

public interface ScoringService {
    Map<String, String> preScore(LoanApplicationRequestDTO loanApplicationRequestDTO);

    Map<String, String> score(ScoringDataDTO scoringDataDTO);

}
