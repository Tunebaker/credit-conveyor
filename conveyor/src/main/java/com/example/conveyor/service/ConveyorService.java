package com.example.conveyor.service;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ConveyorService {

    @Value(value = "${baserate}")
    public Double baseRate;
    private static final Double INSURANCE_COST_FACTOR = 0.005;
    private static final Double INSURANCE_RATE_DISCOUNT = 0.03;     //couldn't be less than baseRate
    private static final Double SALARY_CLIENT_RATE_DISCOUNT = 0.01; //couldn't be less than baseRate


    public List<LoanOfferDTO> composeLoanOfferList(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Long applicationId = 0L;
        List<LoanOfferDTO> loanOfferDTOs = new ArrayList<>();
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, false, false));
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, false, true));
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, true, false));
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, true, true));
        loanOfferDTOs.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());
        return loanOfferDTOs;
    }

    private LoanOfferDTO prescore(LoanApplicationRequestDTO loanApplicationRequestDTO, Long id, Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        MathContext mathContext = MathContext.DECIMAL64;
        BigDecimal insuranceCost = getInsuranceCost(loanApplicationRequestDTO.getAmount(), isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount().add(insuranceCost);
        BigDecimal rate = BigDecimal.valueOf(getPreScoringRate(isInsuranceEnabled, isSalaryClient)).round(new MathContext(3, RoundingMode.HALF_UP));
        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), mathContext);

        BigDecimal monthlyPayment = totalAmount.multiply(monthlyRate)
                .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(BigDecimal.ONE.add(monthlyRate),mathContext)
                        .pow(loanApplicationRequestDTO.getTerm())), 3, RoundingMode.HALF_UP);

        return LoanOfferDTO.builder()
                .applicationId(id)
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(loanApplicationRequestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    private Double getPreScoringRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        double rateCorrection = 0;
        rateCorrection = (isInsuranceEnabled ? rateCorrection - INSURANCE_RATE_DISCOUNT : 0) +
                (isSalaryClient ? rateCorrection - SALARY_CLIENT_RATE_DISCOUNT : 0);
        return baseRate + rateCorrection;
    }

    private Double getScoringRate() {
        double rateAdditional = 0;

        return null; // TODO
    }

    private BigDecimal getInsuranceCost(BigDecimal requestedAmount, Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        return requestedAmount.multiply(BigDecimal.valueOf(isSalaryClient ? 0 :
                isInsuranceEnabled ? INSURANCE_COST_FACTOR : 0));
    }
}
