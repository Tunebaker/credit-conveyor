package com.example.gateway.controller;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.gateway.service.GatewayService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @Schema(description = "Первоначальный запрос на расчёт кредита")
    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO){
        return ResponseEntity.ok(gatewayService.createLoanApplication(loanApplicationRequestDTO));
    }
}
