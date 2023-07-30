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
@Schema(description = "График выплат")
public class PaymentScheduleElement {
    @Schema(description = "Порядковый номер выплаты")
    private Integer number;
    @Schema(description = "Дата выплаты")
    private LocalDate date;
    @Schema(description = "Сумма выплаты")
    private BigDecimal totalPayment;
    @Schema(description = "Сумма выплаты процентов")
    private BigDecimal interestPayment;
    @Schema(description = "Сумма выплаты основного долга")
    private BigDecimal debtPayment;
    @Schema(description = "Остаток долга")
    private BigDecimal remainingDebt;
}
