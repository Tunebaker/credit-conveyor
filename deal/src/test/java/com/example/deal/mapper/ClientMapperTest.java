package com.example.deal.mapper;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.Gender;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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
        ClientEntity client = INSTANCE.finishRegistrationRequestUpdateFields(new ClientEntity(), dto);
        assertEquals("123123", client.getAccount());
        assertEquals(45, client.getDependentAmount());
        assertEquals(Gender.MALE, client.getGender());
        assertEquals(LocalDate.MAX, client.getPassport().getIssueDate());

    }
}