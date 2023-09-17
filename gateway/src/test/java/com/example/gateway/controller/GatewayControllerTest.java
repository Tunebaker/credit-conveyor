package com.example.gateway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class GatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    WireMockServer wireMockServer;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(8082));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8082);
    }

    @AfterEach
    void close() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("createLoanApplication должен возвращать статус ОК и правильный ответ при валидном аргументе ")
    void createLoanApplication() throws Exception {
        String createApplicationRequest = String.join("", Files.readAllLines(Path.of("src/test/resources/create-application-request.json")));
        String createApplicationResponse = String.join("", Files.readAllLines(Path.of("src/test/resources/create-application-response.json")));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodeResponse = mapper.readTree(createApplicationResponse);

        stubFor(WireMock.post(urlMatching("/application"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(jsonNodeResponse)));

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .content(createApplicationRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonNodeResponse.toString()));
    }

    @Test
    @DisplayName("createLoanApplication должен возвращать статус 500 при ошибке сервера")
    void createLoanApplicationWithServerError() throws Exception {
        String createApplicationRequest = String.join("", Files.readAllLines(Path.of("src/test/resources/create-application-request.json")));

        stubFor(WireMock.post(urlMatching("/application"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .content(createApplicationRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    @DisplayName("createLoanApplication должен возвращать статус 400 при невалидном запросе")
    void createLoanApplicationWithBadRequestParams() throws Exception {
        String createApplicationBadRequest = String.join("", Files.readAllLines(Path.of("src/test/resources/create-application-bad-request.json")));

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .content(createApplicationBadRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @DisplayName("chooseOneOffer должен возвращать статус ОК при валидном аргументе")
    void chooseOneOffer() throws Exception {
        String chooseOfferRequest = String.join("",
                Files.readAllLines(Path.of("src/test/resources/apply-offer-request.json")));

        stubFor(WireMock.put(urlMatching("/application/offer"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/application/apply")
                        .content(chooseOfferRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()
                );
    }


    @Test
    @DisplayName("chooseOneOffer должен возвращать статус 400 при невалидном запросе")
    void chooseOneOfferWithBadArgs() throws Exception {
        String chooseOfferRequest = String.join("",
                Files.readAllLines(Path.of("src/test/resources/apply-offer-bad-request.json")));

        mockMvc.perform(MockMvcRequestBuilders.post("/application/apply")
                        .content(chooseOfferRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()
                );
    }

}