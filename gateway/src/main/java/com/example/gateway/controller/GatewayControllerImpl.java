package com.example.gateway.controller;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GatewayControllerImpl implements GatewayController {

    private final GatewayService gatewayService;

    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(gatewayService.createLoanApplication(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> chooseOneOffer(LoanOfferDTO dto) {
        gatewayService.chooseOneOffer(dto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> finishRegistration(Long id) {
        gatewayService.finishRegistration(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> createDocumentRequest(Long id) {
        gatewayService.createDocumentRequest(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> signDocumentRequest(Long id) {
        gatewayService.signDocumentRequest(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> verifySesCodeRequest(Integer sesCode, Long id) {
        gatewayService.verifySesCodeRequest(sesCode, id);
        return ResponseEntity.ok().build();
    }
}
