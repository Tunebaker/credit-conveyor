package com.example.gateway.controller;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "API-Gateway", description = "Микросервисный паттерн API-Gateway для сервисов кредитного конвейера")
public interface GatewayController {

    @Operation(summary = "1. Расчёт 4 предварительных кредитных предложений", tags = "API-Gateway")
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
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    })
    })
    @PostMapping("/application")
    ResponseEntity<List<LoanOfferDTO>> createLoanApplication(@RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO);


    @Operation(summary = "2. Выбор одного из 4 предварительных кредитных предложений", tags = "API-Gateway")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Void.class)))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    })
    })
    @PostMapping("/application/apply")
    ResponseEntity<Void> chooseOneOffer(@RequestBody LoanOfferDTO dto);


    @Operation(summary = "3. Завершение регистрации для окончательного расчёта параметров кредита", tags = "API-Gateway")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Void.class)))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    })
    })
    @PostMapping("/application/registration/{applicationId}")
    ResponseEntity<Void> finishRegistration(@PathVariable("applicationId") Long id,
                                            @RequestBody FinishRegistrationRequestDTO dto);


    @Operation(summary = "4. Запрос на создание документов", tags = "API-Gateway")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Void.class)))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    })
    })
    @PostMapping("/document/{applicationId}")
    ResponseEntity<Void> createDocumentRequest(@PathVariable("applicationId") Long id);


    @Operation(summary = "5. Запрос на подпись документов", tags = "API-Gateway")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Void.class)))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    })
    })
    @PostMapping("/document/{applicationId}/sign")
    ResponseEntity<Void> signDocumentRequest(@PathVariable("applicationId") Long id);


    @Operation(summary = "6. Подпись документов полученным кодом ПЭП", tags = "API-Gateway")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Void.class)))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorAttributes.class))
                    })
    })
    @PostMapping("/document/{applicationId}/sign/code")
    ResponseEntity<Void> verifySesCodeRequest(
            @Parameter(name = "applicationId", description = "номер заявки", required = true) @PathVariable("applicationId") Long applicationId,
            @Parameter(name = "ses-code", description = "код ПЭП", required = true) @RequestHeader(value = "ses-code") Integer sesCode

    );
}
