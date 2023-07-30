package com.example.conveyor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(description = "Данные для окончательного расчёта параметров кредита")
public class ScoringDataDTO {
    @Schema(description = "Сумма кредита с учетом страховки", example = "20000")
    private BigDecimal amount;
    @Schema(description = "Срок кредита в месяцах", example = "36")
    private Integer term;
    @Schema(description = "Фамилия")
    private String firstName;
    @Schema(description = "Имя")
    private String lastName;
    @Schema(description = "Отчество (при наличии)")
    private String middleName;
    @Schema(description = "Пол")
    private Gender gender;
    @Schema(description = "Дата рождения", example = "2000-07-05")
    private LocalDate birthdate;
    @Schema(description = "Серия паспорта", example = "2003")
    private String passportSeries;
    @Schema(description = "Номер паспорта", example = "645979")
    private String passportNumber;
    @Schema(description = "Дата выдачи паспорта", example = "2000-07-05")
    private LocalDate passportIssueDate;
    @Schema(description = "Подразделение, выдавшее паспорт")
    private String passportIssueBranch;
    @Schema(description = "Семейный статус", example = "MARRIED")
    private MaritalStatus maritalStatus;
    @Schema(description = "Количество иждивенцев", example = "1")
    private Integer dependentAmount;
    @Schema(description = "Данные о занятости заемщика")
    private EmploymentDTO employment;
    @Schema(description = "Номер счёта заемщика")
    private String account;
    @Schema(description = "Страховка включена")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Зарплатный клиент")
    private Boolean isSalaryClient;

    @Schema(description = "Пол")
    public enum Gender {
        MALE,
        FEMALE,
        NON_BINARY
    }
    @Schema(description = "Семейный статус")
    public enum MaritalStatus {
        SINGLE,
        MARRIED,
        DIVORCED
    }
}
