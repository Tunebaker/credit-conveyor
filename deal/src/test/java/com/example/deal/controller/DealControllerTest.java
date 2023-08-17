package com.example.deal.controller;

import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.service.DealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {
    @Mock
    private DealService dealService;
    @InjectMocks
    DealController dealController;

    @Test
    @DisplayName("проверка метода getLoanOfferDTOs")
    void getLoanOfferDTOs() {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        List<LoanOfferDTO> dtos = List.of(new LoanOfferDTO(), new LoanOfferDTO());
        doReturn(dtos).when(dealService).createApplication(loanApplicationRequestDTO);

        ResponseEntity<List<LoanOfferDTO>> responseEntity = dealController.getLoanOfferDTOs(loanApplicationRequestDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(dtos, responseEntity.getBody());

    }

    @Test
    @DisplayName("проверка метода chooseOffer")
    void chooseOfferTest() {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        doNothing().when(dealService).applyOffer(loanOfferDTO);

        ResponseEntity<Void> responseEntity = dealController.chooseOffer(loanOfferDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("проверка метода finishRegistration")
    void finishRegistration() {
        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();
        doNothing().when(dealService).calculateCredit(finishRegistrationRequestDTO, 1L);

        ResponseEntity<Void> responseEntity = dealController.finishRegistration(1L, finishRegistrationRequestDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}