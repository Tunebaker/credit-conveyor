package com.example.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Параметры кредита после скоринга")
public class CreditDTO {
    @Schema(description = "Сумма кредита")
    private BigDecimal amount;
    @Schema(description = "Продолжительность выплаты кредита в месяцах")
    private Integer term;
    @Schema(description = "Ежемесячный платёж")
    private BigDecimal monthlyPayment;
    @Schema(description = "Годовая ставка кредита в процентах")
    private BigDecimal rate;
    @Schema(description = "Полная стоимость кредита в процентах")
    private BigDecimal psk;
    @Schema(description = "Страховка включена")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Зарплатный клиент")
    private Boolean isSalaryClient;
    @Schema(description = "График выплат")
    private List<PaymentScheduleElement> paymentSchedule;
}
