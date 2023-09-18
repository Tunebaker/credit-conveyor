package com.example.gateway.controller;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.gateway.service.GatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class GatewayControllerDealTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    GatewayService service;
    Long id = 12L;

    @Test
    void finishRegistration() throws Exception {
        FinishRegistrationRequestDTO dto = new FinishRegistrationRequestDTO();
        doNothing().when(service).finishRegistration(id, dto);

        mockMvc.perform(
                        post("/application/registration/" + id)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }


    @Test
    void finishRegistrationWithBadArgs() throws Exception {
        FinishRegistrationRequestDTO dto = new FinishRegistrationRequestDTO();
        doNothing().when(service).finishRegistration(id, dto);

        mockMvc.perform(
                        post("/application/registration/" + id)
                                .content("qqq eee")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    void createDocumentRequest() throws Exception {
        doNothing().when(service).createDocumentRequest(id);

        mockMvc.perform(
                        post("/document/" + id)
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void signDocumentRequest() throws Exception {
        doNothing().when(service).signDocumentRequest(id);

        mockMvc.perform(
                        post("/document/" + id + "/sign")
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void verifySesCodeRequest() throws Exception {
        doNothing().when(service).verifySesCodeRequest(id, 1234);

        mockMvc.perform(
                        post("/document/" + id + "/sign/code")
                                .header("ses-code", 1234)
                )
                .andExpect(status().isOk())
        ;
    }
}