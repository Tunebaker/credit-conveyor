package com.example.conveyor.service;

import com.example.conveyor.dto.EmploymentDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class Validator {

    private static final String EMAIL_PATTERN = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";
    private static final String NAMES_PATTERN = "[\\w\\.]{2,30}";
    private static final String PASSPORT_SERIES_PATTERN = "[0-9]{4}";
    private static final String PASSPORT_NUMBER_PATTERN = "[0-9]{6}";
    private static final BigDecimal MINIMAL_AMOUNT = new BigDecimal(10000);
    private static final Long MINIMAL_TERM = 6L;
    private static final Long MINIMAL_AGE = 18L;

    public boolean isRequestValid(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        return loanApplicationRequestDTO.getFirstName().matches(NAMES_PATTERN) &&
                loanApplicationRequestDTO.getLastName().matches(NAMES_PATTERN) &&
                (loanApplicationRequestDTO.getMiddleName().matches(NAMES_PATTERN) || loanApplicationRequestDTO.getMiddleName().length() == 0) &&
                (loanApplicationRequestDTO.getAmount().compareTo(MINIMAL_AMOUNT)) >= 0 &&
                loanApplicationRequestDTO.getTerm() >= MINIMAL_TERM &&
                loanApplicationRequestDTO.getBirthdate().plusYears(MINIMAL_AGE).isBefore(LocalDate.now()) &&
                loanApplicationRequestDTO.getEmail().matches(EMAIL_PATTERN) &&
                loanApplicationRequestDTO.getPassportSeries().matches(PASSPORT_SERIES_PATTERN) &&
                loanApplicationRequestDTO.getPassportNumber().matches(PASSPORT_NUMBER_PATTERN);
    }

    public boolean isScoringDataValid(ScoringDataDTO scoringDataDTO){

        return !(scoringDataDTO.getBirthdate().isBefore(LocalDate.now().minusYears(60))
                || scoringDataDTO.getBirthdate().isAfter(LocalDate.now().minusYears(20))
                || scoringDataDTO.getEmployment().getEmploymentStatus().equals(EmploymentDTO.EmploymentStatus.UNEMPLOYED)
                || (scoringDataDTO.getEmployment().getSalary()).multiply(new BigDecimal(20)).compareTo(scoringDataDTO.getAmount()) < 0
                || scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12
                || scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3);

    }
}
