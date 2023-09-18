package com.example.deal.service.impl;

import com.example.deal.exception.SesCodeException;
import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ClientEntity;
import com.example.deal.model.CreditEntity;
import com.example.deal.model.EmailMessage;
import com.example.deal.model.Theme;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.KafkaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREPARE_DOCUMENTS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @InjectMocks
    DocumentServiceImpl documentService;
    @Mock
    KafkaService kafkaService;
    @Mock
    ApplicationRepository applicationRepository;
    @Mock
    ClientRepository clientRepository;
    @Mock
    CreditRepository creditRepository;
    @Mock
    EmailMessage emailMessage;
    Long applicationId;
    Long clientId;
    Long creditId;
    String address;

    @BeforeEach
    void init() {
        address = "qwe@rty.ru";
        applicationId = 5L;
        clientId = 77L;
        creditId = 13L;
    }

    @Test
    void sendFinishRegistrationRequest() {
        emailMessage = new EmailMessage(address, Theme.FINISH_REGISTRATION, applicationId);

        documentService.sendFinishRegistrationRequest(emailMessage);

        verify(kafkaService, times(1)).sendFinishRegistrationRequest(emailMessage);
    }

    @Test
    void sendCreateDocumentRequest() {
        emailMessage = new EmailMessage(address, Theme.CREATE_DOCUMENTS, applicationId);
        documentService.sendCreateDocumentRequest(emailMessage);
        verify(kafkaService, times(1)).sendCreateDocumentRequest(emailMessage);
    }

    @Test
    void sendSendDocumentRequest() {
        emailMessage = new EmailMessage(address, Theme.SEND_DOCUMENTS, applicationId);
        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(clientId)
                .status(PREPARE_DOCUMENTS)
                .build();
        ClientEntity client = ClientEntity.builder()
                .clientId(clientId)
                .email(address)
                .build();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.ofNullable(application));
        when(clientRepository.findById(clientId)).thenReturn(Optional.ofNullable(client));

        documentService.sendSendDocumentRequest(applicationId);

        verify(applicationRepository, times(1)).save(application);
        verify(clientRepository, times(1)).findById(clientId);
        verify(kafkaService, times(1)).sendSendDocumentRequest(emailMessage);
    }

    @Test
    void sendSignDocumentRequest() {
        emailMessage = new EmailMessage(address, Theme.SEND_SES, applicationId);
        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(clientId)
                .build();
        ClientEntity client = ClientEntity.builder()
                .clientId(clientId)
                .email(address)
                .build();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.ofNullable(application));
        when(clientRepository.findById(clientId)).thenReturn(Optional.ofNullable(client));

        documentService.sendSignDocumentRequest(applicationId);

        verify(applicationRepository, times(1)).save(application);
        verify(clientRepository, times(1)).findById(clientId);
        verify(kafkaService, times(1)).sendSignDocumentRequest(emailMessage);

    }

    @Test
    void sendCreditIssueRequest() {
        emailMessage = new EmailMessage(address, Theme.CREDIT_ISSUED, applicationId);
        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(clientId)
                .creditId(creditId)
                .sesCode(9998)
                .build();
        ClientEntity client = ClientEntity.builder()
                .clientId(clientId)
                .email(address)
                .build();
        CreditEntity credit = CreditEntity.builder().creditId(creditId).build();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.ofNullable(application));
        when(clientRepository.findById(clientId)).thenReturn(Optional.ofNullable(client));
        when(creditRepository.findById(creditId)).thenReturn(Optional.ofNullable(credit));

        documentService.sendCreditIssueRequest(applicationId, 9998);

        verify(applicationRepository, times(2)).save(application);
        verify(clientRepository, times(1)).findById(clientId);
        verify(creditRepository, times(1)).findById(creditId);

        verify(kafkaService, times(1)).sendCreditIssueRequest(emailMessage);

    }

    @Test
    void sendCreditIssueRequest_WithWrongSes() {
        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(clientId)
                .sesCode(9998)
                .build();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.ofNullable(application));

        assertThrows(SesCodeException.class, () -> documentService.sendCreditIssueRequest(applicationId, 3335));

    }

    @Test
    void sendApplicationDeniedRequest() {
        emailMessage = new EmailMessage(address, Theme.APPLICATION_DENIED, applicationId);

        documentService.sendApplicationDeniedRequest(emailMessage);

        verify(kafkaService, times(1)).sendApplicationDeniedRequest(emailMessage);
    }
}