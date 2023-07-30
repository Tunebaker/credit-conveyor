package com.example.conveyor.dto;

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
@Schema(description = "Предварительное кредитное предложение")
public class LoanOfferDTO {
    @Schema(description = "Номер предложения")
    private Long applicationId;
    @Schema(description = "Запрошенная сумма кредита")
    private BigDecimal requestedAmount;
    @Schema(description = "Рассчитанная сумма кредита с учетом страховки")
    private BigDecimal totalAmount;
    @Schema(description = "Срок кредита в месяцах")
    private Integer term;
    @Schema(description = "Ежемесячный платёж")
    private BigDecimal monthlyPayment;
    @Schema(description = "Годовая ставка кредита")
    private BigDecimal rate;
    @Schema(description = "Страховка включена")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Зарплатный клиент")
    private Boolean isSalaryClient;
}

