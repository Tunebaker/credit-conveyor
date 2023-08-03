package com.example.conveyor.controller;

import com.example.conveyor.exception.ScoringException;
import com.example.conveyor.model.CreditDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.LoanOfferDTO;
import com.example.conveyor.model.ScoringDataDTO;
import com.example.conveyor.service.ConveyorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
@Tag(name = "контроллер", description = "Методы для расчёта кредита")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @PostMapping("/offers")
    @Operation(summary = "прескоринг: расчёт 4 кредитных предложений")
    public ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOs(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(conveyorService.composeLoanOfferList(loanApplicationRequestDTO));
    }

    @PostMapping("/calculation")
    @Operation(summary = "скоринг: расчёт параметров кредита")
    public ResponseEntity<CreditDTO> getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(conveyorService.composeCreditDTO(scoringDataDTO));
    }

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<ErrorMessage> handleException(ScoringException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
