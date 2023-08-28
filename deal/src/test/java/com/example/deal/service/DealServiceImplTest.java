package com.example.deal.service;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ClientEntity;
import com.example.deal.model.CreditEntity;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.Gender;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.model.MaritalStatus;
import com.example.deal.model.Passport;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.impl.DealServiceImpl;
import com.example.deal.service.client.FeignConveyorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREAPPROVAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {
    @InjectMocks
    DealServiceImpl dealService;
    @Mock
    ClientRepository clientRepository;
    @Mock
    ApplicationRepository applicationRepository;
    @Mock
    CreditRepository creditRepository;
    @Mock
    FeignConveyorService feignConveyorService;

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

        verify(feignConveyorService, times(1)).getLoanOfferDtos(requestDTO);

    }

    @Test
    void applyOfferTest() {

        when(applicationRepository.findById(any())).thenReturn(Optional.of(new ApplicationEntity()));
        dealService.applyOffer(LoanOfferDTO.builder()
                        .isInsuranceEnabled(true)
                        .isSalaryClient(true)
                        .term(7)
                        .monthlyPayment(BigDecimal.ONE)
                        .requestedAmount(BigDecimal.TEN)
                        .totalAmount(BigDecimal.TEN)
                .build());

        ArgumentCaptor<ApplicationEntity> applicationArgumentCaptor = ArgumentCaptor.forClass(ApplicationEntity.class);
        verify(applicationRepository, times(1)).save(applicationArgumentCaptor.capture());
        ApplicationEntity capturedArgument = applicationArgumentCaptor.getValue();
        assertEquals(APPROVED, capturedArgument.getStatus());
        verify(applicationRepository, times(1)).findById(any());
    }

    @Test
    void calculateCreditTest() {
        FinishRegistrationRequestDTO requestDTO = FinishRegistrationRequestDTO.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.DIVORCED)
                .dependentAmount(100)
                .build();
        ApplicationEntity applicationEntity = ApplicationEntity.builder()
                .clientId(2L)
                .applicationId(3L)
                .status(APPROVED)
                .signDate(LocalDateTime.now())
                .appliedOffer(new LoanOfferDTO())
                .build();
        ClientEntity client = ClientEntity.builder()
                .passport(Passport.builder()
                        .number("456456")
                        .series("3211")
                        .issueBranch("")
                        .issueDate(LocalDate.MAX)
                        .build())
                .build();

        when(applicationRepository.findById(any())).thenReturn(Optional.ofNullable(applicationEntity));
        when(clientRepository.findById(any())).thenReturn(Optional.of(client));
        when(creditRepository.save(any())).thenReturn(CreditEntity.builder().creditId(7L).build());

        dealService.calculateCredit(requestDTO, 10L);
        ArgumentCaptor<ClientEntity> clientArgumentCaptor = ArgumentCaptor.forClass(ClientEntity.class);
        verify(clientRepository, times(1)).save(clientArgumentCaptor.capture());

        ClientEntity clientCaptured = clientArgumentCaptor.getValue();
        assertEquals(MaritalStatus.DIVORCED, clientCaptured.getMaritalStatus());
        assertEquals(100, clientCaptured.getDependentAmount());
        assertEquals("3211", clientCaptured.getPassport().getSeries());


        verify(feignConveyorService, times(1)).calculateCredit(any());
    }

}