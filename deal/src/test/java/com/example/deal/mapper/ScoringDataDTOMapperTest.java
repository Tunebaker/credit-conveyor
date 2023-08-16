package com.example.deal.mapper;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.EmploymentDTO;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.Gender;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.model.MaritalStatus;
import com.example.deal.model.Passport;
import com.example.deal.model.ScoringDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.deal.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;
import static com.example.deal.model.EmploymentDTO.PositionEnum.TOP_MANAGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringDataDTOMapperTest {

    LoanOfferDTO appliedOffer;
    ClientEntity client;
    Passport passport;
    FinishRegistrationRequestDTO finishRegistrationRequestDTO;
    ScoringDataDTO scoringDataDTO;

    @BeforeEach
    void init() {
        appliedOffer = new LoanOfferDTO()
                .term(6)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .requestedAmount(BigDecimal.TEN);

        client = ClientEntity.builder()
                .firstName("Sam")
                .lastName("Jones")
                .middleName("Ffff")
                .birthDate(LocalDate.of(1990, 12, 31))
                .passport(Passport.builder()
                        .issueDate(LocalDate.of(2000, 1, 1))
                        .series("3333")
                        .number("333333")
                        .issueBranch("branch")
                        .build())
                .build();

        passport = client.getPassport();

        finishRegistrationRequestDTO = new FinishRegistrationRequestDTO()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.DIVORCED)
                .dependentAmount(5)
                .employment(new EmploymentDTO()
                        .employmentStatus(SELF_EMPLOYED)
                        .position(TOP_MANAGER)
                        .salary(BigDecimal.TEN))
                .account("abcdef17");

        scoringDataDTO = new ScoringDataDTO()
                .amount(appliedOffer.getRequestedAmount())
                .term(appliedOffer.getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(finishRegistrationRequestDTO.getGender())
                .birthdate(client.getBirthDate())
                .passportSeries(passport.getSeries())
                .passportNumber(passport.getNumber())
                .passportIssueDate(passport.getIssueDate())
                .passportIssueBranch(passport.getIssueBranch())
                .maritalStatus(finishRegistrationRequestDTO.getMaritalStatus())
                .dependentAmount(finishRegistrationRequestDTO.getDependentAmount())
                .employment(finishRegistrationRequestDTO.getEmployment())
                .account(finishRegistrationRequestDTO.getAccount())
                .isInsuranceEnabled(appliedOffer.getIsInsuranceEnabled())
                .isSalaryClient(appliedOffer.getIsSalaryClient());

    }

    @Test
    void loanOfferDtoToScoringDataDTO() {
        init();
        ScoringDataDTOMapper INSTANCE = Mappers.getMapper(ScoringDataDTOMapper.class);
        ScoringDataDTO scoringDataDTO = INSTANCE.loanOfferDtoToScoringDataDTO(appliedOffer);

        assertEquals(6, scoringDataDTO.getTerm());
        assertEquals(BigDecimal.TEN, scoringDataDTO.getAmount());
        assertTrue(scoringDataDTO.getIsInsuranceEnabled());
        assertFalse(scoringDataDTO.getIsSalaryClient());
    }

    @Test
    void clientEntityToScoringDataDTOUpdate() {
        init();
        ScoringDataDTOMapper INSTANCE = Mappers.getMapper(ScoringDataDTOMapper.class);
        ScoringDataDTO scoringDataDTO = INSTANCE.clientEntityToScoringDataDTOUpdate(new ScoringDataDTO(), client);

        assertEquals(LocalDate.of(1990, 12, 31), scoringDataDTO.getBirthdate());
        assertEquals("Sam", scoringDataDTO.getFirstName());
        assertEquals("Jones", scoringDataDTO.getLastName());
        assertEquals("Ffff", scoringDataDTO.getMiddleName());
        assertEquals("333333", scoringDataDTO.getPassportNumber());
        assertEquals("3333", scoringDataDTO.getPassportSeries());
        assertEquals("branch", scoringDataDTO.getPassportIssueBranch());
        assertEquals(LocalDate.of(2000, 1, 1), scoringDataDTO.getPassportIssueDate());
    }

    @Test
    void finishRegistrationRequestToScoringDataUpdate() {
        init();
        ScoringDataDTOMapper INSTANCE = Mappers.getMapper(ScoringDataDTOMapper.class);
        ScoringDataDTO scoringDataDTO = INSTANCE.finishRegistrationRequestToScoringDataUpdate(new ScoringDataDTO(),
                finishRegistrationRequestDTO);
        assertEquals(Gender.MALE, scoringDataDTO.getGender());
        assertEquals(MaritalStatus.DIVORCED, scoringDataDTO.getMaritalStatus());
        assertEquals(5, scoringDataDTO.getDependentAmount());
        assertEquals(SELF_EMPLOYED, scoringDataDTO.getEmployment().getEmploymentStatus());
        assertEquals(BigDecimal.TEN, scoringDataDTO.getEmployment().getSalary());
        assertEquals(TOP_MANAGER, scoringDataDTO.getEmployment().getPosition());
        assertEquals(Gender.MALE, scoringDataDTO.getGender());
    }
}