package com.example.application.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void init() {
        WireMockServer wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @Test
    void createApplicationTest() throws Exception {

        String createApplicationRequest = String.join("", Files.readAllLines(Path.of("src/test/resources/create-application-request.json")));
        String createApplicationResponse = String.join("", Files.readAllLines(Path.of("src/test/resources/create-application-response.json")));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodeResponse = mapper.readTree(createApplicationResponse);

        stubFor(WireMock.post(urlMatching("/deal/application"))
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
    void applyOffer() throws Exception {

        String applyOfferRequest = String.join("", Files.readAllLines(Path.of("src/test/resources/apply-offer-request.json")));

        stubFor(WireMock.put(urlMatching("/deal/offer"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

        mockMvc.perform(MockMvcRequestBuilders.put("/application/offer")
                        .content(applyOfferRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}