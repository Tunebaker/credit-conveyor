package com.example.conveyor.service;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
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
}
