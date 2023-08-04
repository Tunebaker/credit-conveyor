package com.example.conveyor.service.impl;

import com.example.conveyor.exception.ScoringException;
import com.example.conveyor.model.*;
import com.example.conveyor.service.ConveyorService;
import com.example.conveyor.service.ScoringService;
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
import java.util.Map;

import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER;
import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;
import static com.example.conveyor.model.EmploymentDTO.PositionEnum.MIDDLE_MANAGER;
import static com.example.conveyor.model.EmploymentDTO.PositionEnum.TOP_MANAGER;
import static com.example.conveyor.model.ScoringDataDTO.GenderEnum.NON_BINARY;
import static com.example.conveyor.model.ScoringDataDTO.MaritalStatusEnum.DIVORCED;
import static com.example.conveyor.model.ScoringDataDTO.MaritalStatusEnum.MARRIED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConveyorServiceImpl implements ConveyorService {

    private final ScoringService scoringService;

    private static final Double BASE_RATE = 0.3;
    private static final MathContext INTERNAL_MATH_CONTEXT = MathContext.DECIMAL64;
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
        log.info("Получена заявка на кредит {}", loanApplicationRequestDTO);
        Map<String, String> validationErrors = scoringService.preScore(loanApplicationRequestDTO);
        if (validationErrors.size() != 0) {
            log.warn("Ошибка валидации: {}", validationErrors);
            throw new ScoringException("Ошибка валидации: " + validationErrors);
        }

        long applicationId = 0L;
        List<LoanOfferDTO> loanOfferDTOs = new ArrayList<>();
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId++, false, false));
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId++, false, true));
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId++, true, false));
        loanOfferDTOs.add(getLoanOffer(loanApplicationRequestDTO, applicationId, true, true));
        loanOfferDTOs.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());
        log.info("Предложения по кредиту : {}", loanOfferDTOs);
        return loanOfferDTOs;
    }

    public CreditDTO composeCreditDTO(ScoringDataDTO scoringDataDTO) {
        log.info("Клиентом выбрано предложение: {}", scoringDataDTO);
        Map<String, String> validationErrors = scoringService.score(scoringDataDTO);
        if (validationErrors.size() != 0) {
            log.warn("Отказ в выдаче кредита. Причина: {}", validationErrors);
            throw new ScoringException("Отказ в выдаче кредита. Причина: " + validationErrors);
        }

        BigDecimal rate = getScoringRate(scoringDataDTO);
        BigDecimal monthlyPayment = calculateMonthlyPayment(rate, scoringDataDTO.getTerm(), scoringDataDTO.getAmount());

        List<PaymentScheduleElement> paymentSchedule = composePaymentSchedule(scoringDataDTO.getAmount(),
                rate, monthlyPayment, scoringDataDTO.getTerm(), LocalDate.now());
        BigDecimal psk = calculatePsk(paymentSchedule, scoringDataDTO.getAmount());

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
        log.info("Одобрен кредит : {}", creditDTO);
        return creditDTO;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {
        log.info("Расчёт месячного платежа по ставке {}, сроку {} месяцев и сумме кредита {} ", rate, term, amount);
        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), INTERNAL_MATH_CONTEXT);
        BigDecimal annuityRatio = (BigDecimal.ONE.add(monthlyRate)).pow(term).multiply(monthlyRate)
                .divide((BigDecimal.ONE.add(monthlyRate)).pow(term).subtract(BigDecimal.ONE), INTERNAL_MATH_CONTEXT);
        BigDecimal monthlyPayment = amount.multiply(annuityRatio).round(INTERNAL_MATH_CONTEXT);
        log.info("Рассчитан ежемесячный платёж: {}", monthlyPayment);
        return monthlyPayment;
    }

    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Long id, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Расчёт кредитного предложения №{} для параметров: заявка {}, страховка {}, зарплатный клиент {}",
                id, loanApplicationRequestDTO, isInsuranceEnabled, isSalaryClient);
        BigDecimal insuranceCost = getInsuranceCost(loanApplicationRequestDTO.getAmount(), isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount().add(insuranceCost);
        BigDecimal rate = getPreScoringRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyPayment = calculateMonthlyPayment(rate, loanApplicationRequestDTO.getTerm(), totalAmount);
        LoanOfferDTO loanOfferDTO = LoanOfferDTO.builder()
                .applicationId(id)
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(loanApplicationRequestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
        log.info("Кредитное предложение №{}: {}", loanOfferDTO.getApplicationId(), loanOfferDTO);
        return loanOfferDTO;
    }

    private BigDecimal getScoringRate(ScoringDataDTO scoringDataDTO) {
        log.info("Вычисляется кредитная ставка для параметров: {}", scoringDataDTO);
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
        BigDecimal rate = preScoringRate.add(rateAdditional, INTERNAL_MATH_CONTEXT);
        log.info("Окончательное значение кредитной ставки: {}", rate);
        return rate;
    }

    private BigDecimal getPreScoringRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Предварительный расчет ставки для значений: страховка {}, зарплатный клиент {}", isInsuranceEnabled, isSalaryClient);
        double rateCorrection = 0;
        rateCorrection = (isInsuranceEnabled ? rateCorrection + INSURANCE_RATE_TERM : 0) +
                (isSalaryClient ? rateCorrection + SALARY_CLIENT_RATE_TERM : 0);
        BigDecimal rate = BigDecimal.valueOf(BASE_RATE + rateCorrection).round(new MathContext(3, RoundingMode.HALF_UP));
        log.info("Предварительная ставка: {}", rate);
        return rate;
    }

    private BigDecimal getInsuranceCost(BigDecimal requestedAmount, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Расчет стоимости страховки: сумма {}, есть страховка {}, зарплатный клиент {}", requestedAmount, isInsuranceEnabled, isSalaryClient);
        BigDecimal insuranceCost = requestedAmount.multiply(BigDecimal.valueOf(isSalaryClient ? 0 :
                isInsuranceEnabled ? NO_INSURANCE_COST_FACTOR : 0));
        log.info("Стоимость страховки: {}", insuranceCost);
        return insuranceCost;
    }

    private List<PaymentScheduleElement> composePaymentSchedule(BigDecimal amount, BigDecimal rate, BigDecimal totalMonthlyPayment,
                                                                Integer term, LocalDate gettingCreditDate) {
        log.info("Формирование графика платежей для данных: сумма {}, ставка {}, месячный платеж {}," +
                "срок {} месяцев, дата получения кредита {}", amount, rate, totalMonthlyPayment, term, gettingCreditDate);
        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();
        BigDecimal remainingDebt = amount;
        LocalDate paymentDate;
        BigDecimal interestPayment;
        BigDecimal debtPayment;

        for (int i = 1; i <= term; i++) {
            paymentDate = gettingCreditDate.plusMonths(i);
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
            log.info("График платежей: {}", paymentScheduleElements);
        }
        return paymentScheduleElements;
    }

    private BigDecimal calculatePsk(List<PaymentScheduleElement> paymentSchedule, BigDecimal amount) {
        log.info("Расчет полной стоимости кредита по графику платежей {}, сумме {}", paymentSchedule, amount);
        BigDecimal totalPaymentSum = BigDecimal.ZERO;
        BigDecimal psk;
        for (PaymentScheduleElement paymentScheduleElement : paymentSchedule) {
            totalPaymentSum = paymentScheduleElement.getTotalPayment().add(totalPaymentSum);
        }

        psk = totalPaymentSum.divide(amount, INTERNAL_MATH_CONTEXT).subtract(BigDecimal.ONE)
                .divide(BigDecimal.valueOf(paymentSchedule.size()), INTERNAL_MATH_CONTEXT)
                .multiply(BigDecimal.valueOf(12)).multiply(BigDecimal.valueOf(100));
        log.info("Полная стоимость кредита: {}", psk);
        return psk;
    }
}


