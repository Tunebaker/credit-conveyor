package com.example.conveyor.service;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.conveyor.model.EmploymentStatus.UNEMPLOYED;

@Service
public class Validator {

    private static final String EMAIL_PATTERN = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";
    private static final String NAMES_PATTERN = "[\\w\\.]{2,30}";
    private static final String PASSPORT_SERIES_PATTERN = "[0-9]{4}";
    private static final String PASSPORT_NUMBER_PATTERN = "[0-9]{6}";
    private static final BigDecimal MINIMAL_AMOUNT = new BigDecimal(10000);
    private static final Long MINIMAL_TERM = 6L;
    private static final Long MINIMAL_AGE = 18L;

    public String preScore(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        StringBuilder errorMessage = new StringBuilder("");
        if (!loanApplicationRequestDTO.getFirstName().matches(NAMES_PATTERN)) {
            errorMessage.append("Имя должно содержать от 2 до 30 латинских букв, ");
        }
        if (!loanApplicationRequestDTO.getLastName().matches(NAMES_PATTERN)) {
            errorMessage.append("Фамилия должна содержать от 2 до 30 латинских букв, ");
        }
        if (loanApplicationRequestDTO.getMiddleName().length() != 0 & !(loanApplicationRequestDTO.getMiddleName().matches(NAMES_PATTERN))) {
            errorMessage.append("Отчество, при наличии, должно содержать от 2 до 30 латинских букв, ");
        }
        if ((loanApplicationRequestDTO.getAmount().compareTo(MINIMAL_AMOUNT)) < 0) {
            errorMessage.append("Сумма кредита должна быть действительным числом, большим или равным 10000, ");
        }
        if (loanApplicationRequestDTO.getTerm() < MINIMAL_TERM) {
            errorMessage.append("Срок кредита должна быть целым числом, большим или равным 6, ");
        }
        if (!loanApplicationRequestDTO.getBirthdate().plusYears(MINIMAL_AGE).isBefore(LocalDate.now())) {
            errorMessage.append("Дата рождения должна быть в числовом формате гггг-мм-дд, не позднее чем за 18 лет до текущего дня, ");
        }
        if (!loanApplicationRequestDTO.getEmail().matches(EMAIL_PATTERN)) {
            errorMessage.append("Email адрес - строка, подходящая под паттерн [\\w\\.]{2,50}@[\\w\\.]{2,20} , ");
        }
        if (!loanApplicationRequestDTO.getPassportSeries().matches(PASSPORT_SERIES_PATTERN)) {
            errorMessage.append("Серия паспорта должна содержать 4 цифры без пробела, ");
        }
        if (!loanApplicationRequestDTO.getPassportNumber().matches(PASSPORT_NUMBER_PATTERN)) {
            errorMessage.append("Серия паспорта должен содержать 6 цифр, ");
        }
        return errorMessage.toString();
    }

    public String score(ScoringDataDTO scoringDataDTO) {
        StringBuilder errorMessage = new StringBuilder("");
        if (scoringDataDTO.getBirthdate().isBefore(LocalDate.now().minusYears(60))) {
            errorMessage.append("возраст более 60 лет, ");
        }
        if (scoringDataDTO.getBirthdate().isAfter(LocalDate.now().minusYears(20))) {
            errorMessage.append("возраст менее 20 лет, ");
        }
        if (((scoringDataDTO.getEmployment().getSalary()).multiply(new BigDecimal(20))).compareTo(scoringDataDTO.getAmount()) < 0) {
            errorMessage.append("сумма займа больше 20 зарплат, ");
        }
        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(UNEMPLOYED)) {
            errorMessage.append("рабочий статус - безработный, ");
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            errorMessage.append("общий стаж работы менее 12 месяцев, ");
        }
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            errorMessage.append("текущий стаж работы менее 3 месяце, ");
        }
        return errorMessage.toString();
    }
}
