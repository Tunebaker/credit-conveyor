package com.example.gateway.controller.impl;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.gateway.controller.GatewayController;
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
    public ResponseEntity<Void> finishRegistration(Long id, FinishRegistrationRequestDTO dto) {
        gatewayService.finishRegistration(id, dto);
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
    public ResponseEntity<Void> verifySesCodeRequest(Long id, Integer sesCode) {
        gatewayService.verifySesCodeRequest(id, sesCode);
        return ResponseEntity.ok().build();
    }
}
