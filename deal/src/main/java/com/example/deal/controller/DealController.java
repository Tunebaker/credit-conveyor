package com.example.deal.controller;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.api.DealApi;
import com.example.deal.service.ClientService;
import com.example.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController implements DealApi {

    private final ClientService clientService; // TODO for testing purpose
    private final DealService dealService;


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
        return ResponseEntity.ok(dealService.createApplication(loanApplicationRequestDTO));
    }



   /////// for testing purpose only /////////
    @GetMapping
    public List<ClientEntity> testMethod(){
        System.out.println("Hi World!");
        System.out.println(clientService.getAllClients());
        return clientService.getAllClients();
    }

    @PostMapping("/save")
    public void testSaveClient(LoanApplicationRequestDTO loanApplicationRequestDTO){
        System.out.println("Saving Client data from LoanApplicationRequestDTO");
        System.out.println(loanApplicationRequestDTO);
        clientService.saveClient(loanApplicationRequestDTO);
    }
    /////// for testing purpose only ////////
}
