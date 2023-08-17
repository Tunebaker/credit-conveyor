package com.example.deal.mapper;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.EmploymentDTO;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.Gender;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientMapperTest {

    @Test
    void finishRegistrationRequestUpdateFields() {
        ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);
        FinishRegistrationRequestDTO dto = FinishRegistrationRequestDTO.builder()
                .account("123123")
                .dependentAmount(45)
                .gender(Gender.MALE)
                .passportIssueDate(LocalDate.MAX)
                .build();
        ClientEntity entity = ClientEntity.builder()
                .employment(new EmploymentDTO()
                        .employerINN("123123132123")
                        .position(EmploymentDTO.PositionEnum.TOP_MANAGER)
                        .employmentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)
                        .salary(BigDecimal.TEN)
                        .workExperienceCurrent(120)
                        .workExperienceTotal(123))
                .build();
        ClientEntity client = INSTANCE.finishRegistrationRequestUpdateFields(entity, dto);
        assertEquals("123123", client.getAccount());
        assertEquals(45, client.getDependentAmount());
        assertEquals(Gender.MALE, client.getGender());
        assertEquals(LocalDate.MAX, client.getPassport().getIssueDate());

    }
}