package com.example.deal.controller;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.repository.ApplicationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.ErrorMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Admin", description = "the admin API")
@RequiredArgsConstructor
public class AdminController {

    private final ApplicationRepository applicationRepository;

    @Operation(summary = "Поиск заявки по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Найденная заявка",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApplicationEntity.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    })
    })
    @GetMapping("/deal/admin/application/{applicationId}")
    public ApplicationEntity getApplicationById(@PathVariable Long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow();
    }


    @Operation(summary = "Поиск всех заявок")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Найденные заявки",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema (schema = @Schema(implementation = ApplicationEntity.class)))
                    }),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка поиска заявок в БД",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    })
    })
    @GetMapping("/deal/admin/application")
    public List<ApplicationEntity> getAllApplications() {
        return applicationRepository.findAll();
    }
}
