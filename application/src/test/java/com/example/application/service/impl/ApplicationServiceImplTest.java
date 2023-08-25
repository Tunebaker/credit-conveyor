package com.example.application.service.impl;

import com.example.application.model.LoanOfferDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationServiceImplTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void init() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @Test
    void createApplicationTest() throws Exception {

        String jsonRequestDTO = String.join("", Files.readAllLines(Path.of("src/test/resources/requestDTO.json")));
        String jsonResponce = String.join("", Files.readAllLines(Path.of("src/test/resources/responceBody.json")));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodeResponce = mapper.readTree(jsonResponce);


        stubFor(WireMock.post(urlMatching("/deal/application"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(jsonNodeResponce)));

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .content(jsonRequestDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonNodeResponce.toString()));
    }

    @Test
    void applyOffer() throws Exception {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.TEN)
                .totalAmount(BigDecimal.TEN)
                .term(45)
                .monthlyPayment(BigDecimal.TEN)
                .rate(BigDecimal.ONE)
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String offerJson = ow.writeValueAsString(loanOfferDTO);

        stubFor(WireMock.put(urlMatching("/deal/offer"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

        mockMvc.perform(MockMvcRequestBuilders.put("/application/offer")
                        .content(offerJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}