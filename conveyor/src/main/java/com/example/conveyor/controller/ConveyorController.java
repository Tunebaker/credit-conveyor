package com.example.conveyor.controller;

import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.service.ConveyorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@AllArgsConstructor
@Tag(name = "контроллер", description = "Методы для расчёта кредита")
public class ConveyorController {

    ConveyorService conveyorService;

    @PostMapping("/offers")
    @Operation(summary = "прескоринг: расчёт 4 кредитных предложений")
    public List<LoanOfferDTO> getLoanOfferDTOs(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return conveyorService.composeLoanOfferList(loanApplicationRequestDTO);
    }

    @PostMapping("/calculation")
    @Operation(summary = "скоринг: расчёт параметров кредита")
    public CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        System.out.println(scoringDataDTO);
        return conveyorService.composeCreditDTO(scoringDataDTO); 
    }
}
