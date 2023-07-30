package com.example.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Первичный запрос на кредит")
public class LoanApplicationRequestDTO {
    @Schema(description = "Желаемая сумма кредита")
    private BigDecimal amount;
    @Schema(description = "Желаемый срок кредита в месяцах")
    private Integer term;
    @Schema(description = "Имя заемщика")
    private String firstName;
    @Schema(description = "Фамилия заемщика")
    private String lastName;
    @Schema(description = "Отчество заемщика (при наличии)")
    private String middleName;
    @Schema(description = "E-mail заемщика")
    private String email;
    @Schema(description = "Дата рождения  заемщика", example = "2000-01-01")
    private LocalDate birthdate;
    @Schema(description = "Серия паспорта", example = "2003")
    private String passportSeries;
    @Schema(description = "Номер паспорта", example = "645979")
    private String passportNumber;
}
