package com.example.application.service;

import com.example.application.model.LoanApplicationRequestDTO;

import java.util.Map;

public interface PreScoringService {
    Map<String, String> preScore(LoanApplicationRequestDTO loanApplicationRequestDTO);

}
