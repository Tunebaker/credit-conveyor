package com.example.deal.controller;

import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.api.DealApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController implements DealApi {

    @Override
    public ResponseEntity<Void> chooseOffer(LoanOfferDTO loanOfferDTO) {
        return DealApi.super.chooseOffer(loanOfferDTO);
    }

    @Override
    public ResponseEntity<Void> finishRegistration(BigDecimal applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        return DealApi.super.finishRegistration(applicationId, finishRegistrationRequestDTO);
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOs(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        return DealApi.super.getLoanOfferDTOs(loanApplicationRequestDTO);
    }
}
