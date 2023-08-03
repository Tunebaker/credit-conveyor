package com.example.conveyor.controller;

import com.example.conveyor.model.CreditDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.LoanOfferDTO;
import com.example.conveyor.model.ScoringDataDTO;
import com.example.conveyor.service.ConveyorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
public class ConveyorControllerImpl implements ConveyorController {

    private final ConveyorService conveyorService;


    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDTO>> getLoanOfferDTOs(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(conveyorService.composeLoanOfferList(loanApplicationRequestDTO));
    }

    @PostMapping("/calculation")
    public ResponseEntity<CreditDTO> getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(conveyorService.composeCreditDTO(scoringDataDTO));
    }

//    @ExceptionHandler(ScoringException.class)
//    public ResponseEntity<ErrorMessage> handleException(ScoringException exception) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorMessage(exception.getMessage()));
//    }
}
