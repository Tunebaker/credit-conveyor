package com.example.conveyor.controller;

import com.example.conveyor.api.ConveyorApi;
import com.example.conveyor.model.CreditDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.LoanOfferDTO;
import com.example.conveyor.model.ScoringDataDTO;
import com.example.conveyor.service.ConveyorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConveyorController implements ConveyorApi {

    private final ConveyorService conveyorService;

    @Override
    public ResponseEntity<CreditDTO> getCreditDTO(ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(conveyorService.composeCreditDTO(scoringDataDTO));
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOs(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(conveyorService.composeLoanOfferList(loanApplicationRequestDTO));
    }
}
