package com.example.conveyor.service.impl;

import com.example.conveyor.dto.*;
import com.example.conveyor.exception.ScoringException;
import com.example.conveyor.service.ConveyorService;
import com.example.conveyor.service.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.conveyor.model.EmploymentStatus.BUSINESS_OWNER;
import static com.example.conveyor.model.EmploymentStatus.SELF_EMPLOYED;
import static com.example.conveyor.model.Gender.NON_BINARY;
import static com.example.conveyor.model.MaritalStatus.DIVORCED;
import static com.example.conveyor.model.MaritalStatus.MARRIED;
import static com.example.conveyor.model.Position.MIDDLE_MANAGER;
import static com.example.conveyor.model.Position.TOP_MANAGER;


@Service
@RequiredArgsConstructor
@Slf4j
public class ConveyorServiceImpl implements ConveyorService {

    private static final Double BASE_RATE = 0.3;
    private final Validator validator;
    private static final MathContext INTERNAL_MATH_CONTEXT = MathContext.DECIMAL64;
    private static final MathContext OUT_MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);
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

    public List<LoanOfferDTO> composeLoanOfferList(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("received loanApplicationRequest {}", loanApplicationRequestDTO);
        if (!validator.isRequestValid(loanApplicationRequestDTO)) {
            log.warn("Validation is failed for {}", loanApplicationRequestDTO);
            throw new ScoringException("oops! One or more parameters of loan application request are not valid");
        }

        long applicationId = 0L;
        List<LoanOfferDTO> loanOfferDTOs = new ArrayList<>();
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId++, false, false));
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId++, false, true));
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId++, true, false));
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId, true, true));
        loanOfferDTOs.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());
        log.info("Prescoring offer: {}", loanOfferDTOs);
        return loanOfferDTOs;
    }

    public CreditDTO composeCreditDTO(ScoringDataDTO scoringDataDTO) {
        log.info("Received scoring data: {}", scoringDataDTO);
        if (!validator.isScoringDataValid(scoringDataDTO)) {
            log.warn("Validation is failed for {}", scoringDataDTO);
            throw new ScoringException("One or more conditions are not fulfilled, credit is denied");
        }

        BigDecimal rate = getScoringRate(scoringDataDTO);
        BigDecimal monthlyPayment = calculateMonthlyPayment(rate, scoringDataDTO.getTerm(), scoringDataDTO.getAmount());

        List<PaymentScheduleElement> paymentSchedule = composePaymentSchedule(scoringDataDTO.getAmount(),
                rate, monthlyPayment, scoringDataDTO.getTerm(), LocalDate.now());
        BigDecimal psk = calculatePsk(paymentSchedule, scoringDataDTO.getAmount(), scoringDataDTO.getTerm());

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate.multiply(BigDecimal.valueOf(100)))
                .psk(psk)
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();
        log.info("Credit scoring : {}", creditDTO);
        return creditDTO;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {
        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), INTERNAL_MATH_CONTEXT);
        BigDecimal annuityRatio = (BigDecimal.ONE.add(monthlyRate)).pow(term).multiply(monthlyRate)
                .divide((BigDecimal.ONE.add(monthlyRate)).pow(term).subtract(BigDecimal.ONE), INTERNAL_MATH_CONTEXT);
        return amount.multiply(annuityRatio).round(INTERNAL_MATH_CONTEXT);
    }

    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Long id, Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        BigDecimal insuranceCost = getInsuranceCost(loanApplicationRequestDTO.getAmount(), isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount().add(insuranceCost);
        BigDecimal rate = getPreScoringRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyPayment = calculateMonthlyPayment(rate, loanApplicationRequestDTO.getTerm(), totalAmount);
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
        return preScoringRate.add(rateAdditional, INTERNAL_MATH_CONTEXT);
    }

    private BigDecimal getPreScoringRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        double rateCorrection = 0;
        rateCorrection = (isInsuranceEnabled ? rateCorrection + INSURANCE_RATE_TERM : 0) +
                (isSalaryClient ? rateCorrection + SALARY_CLIENT_RATE_TERM : 0);
        return BigDecimal.valueOf(BASE_RATE + rateCorrection).round(new MathContext(3, RoundingMode.HALF_UP));
    }

    private BigDecimal getInsuranceCost(BigDecimal requestedAmount, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        return requestedAmount.multiply(BigDecimal.valueOf(isSalaryClient ? 0 :
                isInsuranceEnabled ? NO_INSURANCE_COST_FACTOR : 0));
    }

    private List<PaymentScheduleElement> composePaymentSchedule(BigDecimal amount, BigDecimal rate, BigDecimal totalMonthlyPayment,
                                                                Integer term, LocalDate firstPaymentDate) {
        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();
        BigDecimal remainingDebt = amount;
        LocalDate paymentDate;
        BigDecimal interestPayment;
        BigDecimal debtPayment;

        for (int i = 1; i <= term; i++) {
            paymentDate = firstPaymentDate.plusMonths(i);
            interestPayment = remainingDebt.multiply(rate).multiply(BigDecimal.valueOf(paymentDate.lengthOfMonth()))
                    .divide(BigDecimal.valueOf(paymentDate.lengthOfYear()), INTERNAL_MATH_CONTEXT);
            debtPayment = totalMonthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);

            if (i == term) {
                totalMonthlyPayment = totalMonthlyPayment.add(remainingDebt);
                remainingDebt = BigDecimal.ZERO;
            }

            paymentScheduleElements.add(PaymentScheduleElement.builder()
                    .number(i)
                    .date(paymentDate)
                    .totalPayment(totalMonthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(totalMonthlyPayment.subtract(interestPayment))
                    .remainingDebt(remainingDebt)
                    .build());
        }
        return paymentScheduleElements;
    }

    private BigDecimal calculatePsk(List<PaymentScheduleElement> paymentSchedule, BigDecimal amount, Integer term) {
        BigDecimal totalPaymentSum = BigDecimal.ZERO;
        BigDecimal psk;
        for (PaymentScheduleElement paymentScheduleElement : paymentSchedule) {
            totalPaymentSum = paymentScheduleElement.getTotalPayment().add(totalPaymentSum);
        }

        psk = totalPaymentSum.divide(amount, INTERNAL_MATH_CONTEXT).subtract(BigDecimal.ONE)
                .divide(BigDecimal.valueOf(term), INTERNAL_MATH_CONTEXT)
                .multiply(BigDecimal.valueOf(12)).multiply(BigDecimal.valueOf(100));
        return psk;
    }
}


