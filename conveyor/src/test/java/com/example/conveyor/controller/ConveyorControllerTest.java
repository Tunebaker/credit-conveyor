package com.example.conveyor.controller;

import com.example.conveyor.model.CreditDTO;
import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.ScoringDataDTO;
import com.example.conveyor.service.ConveyorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ConveyorControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ConveyorService conveyorService;

    @Test
    void getCreditDTO() throws Exception {
        ScoringDataDTO dto = new ScoringDataDTO();
        when(conveyorService.composeCreditDTO(dto)).thenReturn(new CreditDTO());

        mockMvc.perform(
                        post("/conveyor/offers")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void getLoanOfferDTOs() throws Exception {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO();
        when(conveyorService.composeLoanOfferList(dto)).thenReturn(new ArrayList<>());

        mockMvc.perform(
                        post("/conveyor/calculation")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }
}