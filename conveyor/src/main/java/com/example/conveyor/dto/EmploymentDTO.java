package com.example.conveyor.dto;

import com.example.conveyor.model.EmploymentStatus;
import com.example.conveyor.model.Position;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные о занятости заемщика")
public class EmploymentDTO {
    @Schema(description = "Статус занятости", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;
    @Schema(description = "ИНН")
    private String employerINN;
    @Schema(description = "Заработная плата за месяц", example = "10000" )
    private BigDecimal salary;
    @Schema(description = "Должность", example = "ORDINARY_WORKER")
    private Position position;
    @Schema(description = "Общий стаж работы в месяцах", example = "120")
    private Integer workExperienceTotal;
    @Schema(description = "Стаж работы на текущем месте в месяцах", example = "24")
    private Integer workExperienceCurrent;
}


