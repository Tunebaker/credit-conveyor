package com.example.conveyor.service;

import com.example.conveyor.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.conveyor.dto.EmploymentDTO.EmploymentStatus.BUSINESS_OWNER;
import static com.example.conveyor.dto.EmploymentDTO.EmploymentStatus.SELF_EMPLOYED;
import static com.example.conveyor.dto.EmploymentDTO.Position.MIDDLE_MANAGER;
import static com.example.conveyor.dto.EmploymentDTO.Position.TOP_MANAGER;
import static com.example.conveyor.dto.ScoringDataDTO.Gender.NON_BINARY;
import static com.example.conveyor.dto.ScoringDataDTO.MaritalStatus.DIVORCED;
import static com.example.conveyor.dto.ScoringDataDTO.MaritalStatus.MARRIED;


@Service
@Slf4j
public class ConveyorService {

    @Value(value = "${baserate}")
    public Double baseRate;
    public Validator validator;
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    private static final Double NO_INSURANCE_COST_FACTOR = 0.005;
    private static final Double INSURANCE_RATE_TERM = -0.03;
    private static final Double SALARY_CLIENT_RATE_TERM = -0.01;
    private static final BigDecimal SELF_EMPLOYED_RATE_TERM = BigDecimal.valueOf(0.01);
    private static final BigDecimal BUSINESS_OWNER_RATE_TERM = BigDecimal.valueOf(0.03);
    private static final BigDecimal MIDDLE_MANAGER_RATE_TERM = BigDecimal.valueOf(-0.02);
    private static final BigDecimal TOP_MANAGER_RATE_TERM = BigDecimal.valueOf(-0.04);
    private static final BigDecimal MARRIED_RATE_TERM = BigDecimal.valueOf(-0.03);
    private static final BigDecimal DIVORCED_RATE_TERM = BigDecimal.valueOf(0.01);
    private static final BigDecimal MANY_DEPENDENT_RATE_TERM = BigDecimal.valueOf(0.01);
    private static final Integer MAX_DEPENDENT_AMOUNT = 1;
    private static final BigDecimal NON_BINARY_RATE_TERM = BigDecimal.valueOf(0.03);
    private static final BigDecimal MIDDLE_AGE_RATE_TERM = BigDecimal.valueOf(-0.03);

    @Autowired
    public ConveyorService(Validator validator) {
        this.validator = validator;
    }

    public List<LoanOfferDTO> composeLoanOfferList(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("received loanApplicationRequest {}", loanApplicationRequestDTO);
        if (!validator.isRequestValid(loanApplicationRequestDTO)) {
            log.warn("Validation is failed for {}", loanApplicationRequestDTO);
            throw new IllegalArgumentException("One or more parameters of loan application request are not valid");
        }

        Long applicationId = 0L;
        List<LoanOfferDTO> loanOfferDTOs = new ArrayList<>();
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, false, false));
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, false, true));
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, true, false));
        loanOfferDTOs.add(prescore(loanApplicationRequestDTO, applicationId++, true, true));
        loanOfferDTOs.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());
        log.info("Prescoring offer: {}", loanOfferDTOs);
        return loanOfferDTOs;
    }

    public CreditDTO composeCreditDTO(ScoringDataDTO scoringDataDTO) {
        log.info("Received scoring data: {}", scoringDataDTO);
        if (scoringDataDTO.getBirthdate().isBefore(LocalDate.now().minusYears(60))
                || scoringDataDTO.getBirthdate().isAfter(LocalDate.now().minusYears(20))
                || scoringDataDTO.getEmployment().getEmploymentStatus().equals(EmploymentDTO.EmploymentStatus.UNEMPLOYED)
                || (scoringDataDTO.getEmployment().getSalary()).multiply(new BigDecimal(20)).compareTo(scoringDataDTO.getAmount()) < 0
                || scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12
                || scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3
        ) {
            log.warn("Validation is failed for {}", scoringDataDTO);
            throw new RuntimeException("one or more conditions are not fulfilled, credit is denied");
        }
        BigDecimal rate = getScoringRate(scoringDataDTO);
        BigDecimal psk = scoringDataDTO.getAmount().multiply(BigDecimal.ONE.add(rate));
        BigDecimal monthlyPayment = psk.divide(new BigDecimal(scoringDataDTO.getTerm()), MATH_CONTEXT);
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();
        log.info("Credit scoring : {}", creditDTO);
        return creditDTO;
    }

    private LoanOfferDTO prescore(LoanApplicationRequestDTO loanApplicationRequestDTO, Long id, Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        BigDecimal insuranceCost = getInsuranceCost(loanApplicationRequestDTO.getAmount(), isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount().add(insuranceCost);
        BigDecimal rate = getPreScoringRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), MATH_CONTEXT);

        BigDecimal monthlyPayment = totalAmount.multiply(monthlyRate)
                .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(BigDecimal.ONE.add(monthlyRate), MATH_CONTEXT)
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

    private BigDecimal getScoringRate(ScoringDataDTO scoringDataDTO) {
        BigDecimal rateAdditional = BigDecimal.valueOf(0);
        BigDecimal preScoringRate = getPreScoringRate(scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient());

        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(SELF_EMPLOYED)) {
            rateAdditional = rateAdditional.add(SELF_EMPLOYED_RATE_TERM);
        }
        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(BUSINESS_OWNER)) {
            rateAdditional = rateAdditional.add(BUSINESS_OWNER_RATE_TERM);
        }
        if (scoringDataDTO.getEmployment().getPosition().equals(MIDDLE_MANAGER)) {
            rateAdditional = rateAdditional.add(MIDDLE_MANAGER_RATE_TERM);
        }
        if (scoringDataDTO.getEmployment().getPosition().equals(TOP_MANAGER)) {
            rateAdditional = rateAdditional.add(TOP_MANAGER_RATE_TERM);
        }
        if (scoringDataDTO.getMaritalStatus().equals(MARRIED)) {
            rateAdditional = rateAdditional.add(MARRIED_RATE_TERM);
        }
        if (scoringDataDTO.getDependentAmount() > MAX_DEPENDENT_AMOUNT) {
            rateAdditional = rateAdditional.add(MANY_DEPENDENT_RATE_TERM);
        }
        if (scoringDataDTO.getMaritalStatus().equals(DIVORCED)) {
            rateAdditional = rateAdditional.add(DIVORCED_RATE_TERM);
        }
        if (scoringDataDTO.getGender().equals(NON_BINARY)) {
            rateAdditional = rateAdditional.add(NON_BINARY_RATE_TERM);
        } else if (scoringDataDTO.getBirthdate().isBefore(LocalDate.now().minusYears(55)) &&
                scoringDataDTO.getBirthdate().isAfter(LocalDate.now().minusYears(30))) {
            rateAdditional = rateAdditional.add(MIDDLE_AGE_RATE_TERM);
        }
        return preScoringRate.add(rateAdditional, MATH_CONTEXT);
    }

    private BigDecimal getPreScoringRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        double rateCorrection = 0;
        rateCorrection = (isInsuranceEnabled ? rateCorrection + INSURANCE_RATE_TERM : 0) +
                (isSalaryClient ? rateCorrection + SALARY_CLIENT_RATE_TERM : 0);
        return BigDecimal.valueOf(baseRate + rateCorrection).round(new MathContext(3, RoundingMode.HALF_UP));
    }

    private BigDecimal getInsuranceCost(BigDecimal requestedAmount, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        return requestedAmount.multiply(BigDecimal.valueOf(isSalaryClient ? 0 :
                isInsuranceEnabled ? NO_INSURANCE_COST_FACTOR : 0));
    }
}
