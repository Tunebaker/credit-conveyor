package com.example.deal.controller;

import com.example.deal.api.DealApi;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.service.interfaces.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController implements DealApi {

    private final DealService dealService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOs(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(dealService.createApplication(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> chooseOffer(LoanOfferDTO loanOfferDTO) {
        return DealApi.super.chooseOffer(loanOfferDTO);
    }

    @Override
    public ResponseEntity<Void> finishRegistration(BigDecimal applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        return DealApi.super.finishRegistration(applicationId, finishRegistrationRequestDTO);
    }
}
