package com.example.deal.controller;

import com.example.deal.api.DealApi;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.service.interfaces.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
        dealService.applyOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> finishRegistration(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        dealService.calculateCredit(finishRegistrationRequestDTO, applicationId);
        return ResponseEntity.ok().build();
    }
}
