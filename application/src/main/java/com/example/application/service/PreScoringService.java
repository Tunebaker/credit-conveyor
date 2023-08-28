package com.example.application.service;

import com.example.application.model.LoanApplicationRequestDTO;

import java.util.Map;

public interface PreScoringService {
    void preScore(LoanApplicationRequestDTO loanApplicationRequestDTO);

}
