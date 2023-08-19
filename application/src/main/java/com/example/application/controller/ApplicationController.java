package com.example.application.controller;

import com.example.application.api.ApplicationApi;
import com.example.application.model.LoanOfferDTO;
import com.example.application.service.impl.ApplicationServiceImpl;
import com.example.application.model.LoanApplicationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationServiceImpl applicationServiceImpl;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(applicationServiceImpl.createApplication(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> applyOffer(LoanOfferDTO loanOfferDTO) {
        applicationServiceImpl.applyOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }
}
