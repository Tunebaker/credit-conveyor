package com.example.conveyor.service;

import com.example.conveyor.dto.ScoringDataDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ConveyorServiceTest {

    @Test
    void composeCreditDTO() {
        Validator validator = new Validator();
        ConveyorService conveyorService = new ConveyorService(validator);
        conveyorService.composeCreditDTO(ScoringDataDTO.builder()
                        .birthdate(LocalDate.now().minusYears(55))
                .build());
    }
}