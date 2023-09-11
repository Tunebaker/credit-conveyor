package com.example.gateway.controller;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "API-Gateway", description = "Микросервисный паттерн API-Gateway для сервисов кредитного конвейера")
public interface GatewayController {

    @Operation(summary = "Рассчитать 4 предварительных кредитных предложения", tags = "API-Gateway")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат предварительного расчёта",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LoanOfferDTO.class)))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка запроса"
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/application")
    ResponseEntity<List<LoanOfferDTO>> createLoanApplication(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);

    @PostMapping("/application/apply")
    ResponseEntity<Void> chooseOneOffer(LoanOfferDTO dto);

    @PostMapping("/application/registration/{applicationId}")
    ResponseEntity<Void> finishRegistration(Long id);

    @PostMapping("/document/{applicationId}")
    ResponseEntity<Void> createDocumentRequest(Long id);

    @PostMapping("/document/{applicationId}/sign")
    ResponseEntity<Void> signDocumentRequest(Long id);

    @PostMapping("/document/{applicationId}/sign/code")
    ResponseEntity<Void> verifySesCodeRequest(Integer sesCode, Long id);
}
