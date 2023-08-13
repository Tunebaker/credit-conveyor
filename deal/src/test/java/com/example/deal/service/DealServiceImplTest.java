package com.example.deal.service;

import com.example.deal.model.*;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.util.FeignServiceUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREAPPROVAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {
    @InjectMocks
    DealServiceImpl dealService;

    @Mock
    ClientRepository clientRepository;
    @Mock
    ApplicationRepository applicationRepository;
    @Mock
    FeignServiceUtil feignServiceUtil;

    @Test
    void createApplicationTest() {

        LoanApplicationRequestDTO requestDTO = LoanApplicationRequestDTO.builder()
                .term(6)
                .passportSeries("123456")
                .passportNumber("1234")
                .firstName("Piotr")
                .lastName("Petrov")
                .email("qwe@rty")
                .amount(new BigDecimal(10000))
                .birthdate(LocalDate.parse("2000-01-01"))
                .build();

        ClientEntity client = ClientEntity.builder()
                .lastName("Petrov")
                .firstName("Piotrkdjnfbkjndb")
                .birthDate(LocalDate.parse("2000-01-01"))
                .email("qwe@rty")
                .passport(Passport.builder()
                        .series("123456")
                        .number("1234")
                        .build())
                .build();

        when(clientRepository.save(any())).thenReturn(client);

        dealService.createApplication(requestDTO);

        ArgumentCaptor<ClientEntity> clientArgumentCaptor = ArgumentCaptor.forClass(ClientEntity.class);
        verify(clientRepository, times(1)).save(clientArgumentCaptor.capture());
        ClientEntity clientCapturedArgument = clientArgumentCaptor.getValue();
        assertEquals("Piotr", clientCapturedArgument.getFirstName());
        assertEquals("1234", clientCapturedArgument.getPassport().getNumber());

        ArgumentCaptor<ApplicationEntity> applicationArgumentCaptor = ArgumentCaptor.forClass(ApplicationEntity.class);
        verify(applicationRepository, times(1)).save(applicationArgumentCaptor.capture());
        ApplicationEntity capturedArgument = applicationArgumentCaptor.getValue();
        assertEquals(PREAPPROVAL, capturedArgument.getStatus());

        verify(feignServiceUtil, times(1)).getLoanOfferDtos(requestDTO);

    }

    @Test
    void applyOfferTest() {

        when(applicationRepository.findById(any())).thenReturn(Optional.of(new ApplicationEntity()));
        dealService.applyOffer(new LoanOfferDTO());

        ArgumentCaptor<ApplicationEntity> applicationArgumentCaptor = ArgumentCaptor.forClass(ApplicationEntity.class);
        verify(applicationRepository, times(1)).save(applicationArgumentCaptor.capture());
        ApplicationEntity capturedArgument = applicationArgumentCaptor.getValue();
        assertEquals(APPROVED, capturedArgument.getStatus());
        verify(applicationRepository, times(1)).findById(any());
    }

    @Test
    void calculateCredit() {
    }
}