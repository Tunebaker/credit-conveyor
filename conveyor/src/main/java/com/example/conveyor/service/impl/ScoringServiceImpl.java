package com.example.conveyor.service.impl;

import com.example.conveyor.model.LoanApplicationRequestDTO;
import com.example.conveyor.model.ScoringDataDTO;
import com.example.conveyor.service.ScoringService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.example.conveyor.model.EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED;


@Service
public class ScoringServiceImpl implements ScoringService {

    private static final String EMAIL_PATTERN = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";
    private static final String NAMES_PATTERN = "[\\w\\.]{2,30}";
    private static final String PASSPORT_SERIES_PATTERN = "[0-9]{4}";
    private static final String PASSPORT_NUMBER_PATTERN = "[0-9]{6}";
    private static final BigDecimal MINIMAL_AMOUNT = new BigDecimal(10000);
    private static final Long MINIMAL_TERM = 6L;
    private static final Long MINIMAL_AGE = 18L;

    public Map<String, String> preScore(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Map<String, String> validationErrors = new HashMap<>();
        if (!loanApplicationRequestDTO.getFirstName().matches(NAMES_PATTERN)) {
            validationErrors.put("Имя", "должно содержать от 2 до 30 латинских букв");
        }
        if (!loanApplicationRequestDTO.getLastName().matches(NAMES_PATTERN)) {
            validationErrors.put("Фамилия", "должна содержать от 2 до 30 латинских букв");
        }
        if (loanApplicationRequestDTO.getMiddleName().length() != 0 & !(loanApplicationRequestDTO.getMiddleName().matches(NAMES_PATTERN))) {
            validationErrors.put("Отчество", "при наличии должно содержать от 2 до 30 латинских букв");
        }
        if ((loanApplicationRequestDTO.getAmount().compareTo(MINIMAL_AMOUNT)) < 0) {
            validationErrors.put("Сумма кредита", "должна быть действительным числом, большим или равным 10000");
        }
        if (loanApplicationRequestDTO.getTerm() < MINIMAL_TERM) {
            validationErrors.put("Срок кредита", "должен быть целым числом, большим или равным 6");
        }
        if (!loanApplicationRequestDTO.getBirthdate().plusYears(MINIMAL_AGE).isBefore(LocalDate.now())) {
            validationErrors.put("Дата рождения", "должна быть в числовом формате гггг-мм-дд, не позднее чем за 18 лет до текущего дня");
        }
        if (!loanApplicationRequestDTO.getEmail().matches(EMAIL_PATTERN)) {
            validationErrors.put("Email адрес", "строка, подходящая под паттерн [\\w\\.]{2,50}@[\\w\\.]{2,20}");
        }
        if (!loanApplicationRequestDTO.getPassportSeries().matches(PASSPORT_SERIES_PATTERN)) {
            validationErrors.put("Серия паспорта", "должна содержать 4 цифры без пробела");
        }
        if (!loanApplicationRequestDTO.getPassportNumber().matches(PASSPORT_NUMBER_PATTERN)) {
            validationErrors.put("Номер паспорта", "должен содержать 6 цифр");
        }
        return validationErrors;
    }

    public Map<String, String> score(ScoringDataDTO scoringDataDTO) {
        Map<String, String> validationErrors = new HashMap<>();
        if (scoringDataDTO.getBirthdate().isBefore(LocalDate.now().minusYears(60))) {
            validationErrors.put("возраст", "более 60 лет");
        }
        if (scoringDataDTO.getBirthdate().isAfter(LocalDate.now().minusYears(20))) {
            validationErrors.put("возраст", "менее 20 лет");
        }
        if (((scoringDataDTO.getEmployment().getSalary()).multiply(new BigDecimal(20))).compareTo(scoringDataDTO.getAmount()) < 0) {
            validationErrors.put("сумма займа", "больше 20 зарплат");
        }
        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(UNEMPLOYED)) {
            validationErrors.put("рабочий статус", "безработный");
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            validationErrors.put("общий стаж работы", "менее 12 месяцев");
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            validationErrors.put("текущий стаж работы", "менее 3 месяцев");
        }
        return validationErrors;
    }
}
