package com.example.conveyor.controller;

import com.example.conveyor.model.CreditDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.LoanOfferDTO;
import com.example.conveyor.model.ScoringDataDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Контроллер микросервиса Конвейер", description = "Методы для расчёта кредита")
public interface ConveyorController {

    @Operation(summary = "прескоринг: расчёт 4 кредитных предложений")
    ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOs(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @Operation(summary = "скоринг: расчёт параметров кредита")
    ResponseEntity<CreditDTO> getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO);
}
