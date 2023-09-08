package com.example.dossier.service;

import com.example.dossier.model.EmailMessage;
import com.example.dossier.model.Theme;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaListenerServiceTest {

    @InjectMocks
    KafkaListenerService kafkaListenerService;
    @Mock
    EmailSenderService emailSenderService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    EmailMessage emailMessage;
    String messageJson;
    Long appId;

    @BeforeEach
    void init() throws JsonProcessingException {
        appId = 2L;
        messageJson = "{\"some field\":\"some value\"}";
        emailMessage = new EmailMessage("alandrr@ya.ru", Theme.CREDIT_ISSUED, appId);
        when(objectMapper.readValue(messageJson, EmailMessage.class)).thenReturn(emailMessage);
    }

    @Test
    void sendFinishRegistrationRequest() throws JsonProcessingException {
        String text = "Уважаемый клиент, завершите Вашу регистрацию по заявке №";

        kafkaListenerService.sendFinishRegistrationRequest(messageJson);

        verify(objectMapper, times(1)).readValue(messageJson, EmailMessage.class);
        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);
    }

    @Test
    void sendCreateDocumentsRequest() throws JsonProcessingException {
        String text = "Уважаемый клиент, отправьте запрос для того, чтобы перейти к оформлению документов по заявке №";

        kafkaListenerService.sendCreateDocumentsRequest(messageJson);

        verify(objectMapper, times(1)).readValue(messageJson, EmailMessage.class);
        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);
    }

    @Test
    void sendSendDocumentsRequest() throws JsonProcessingException {
        String text = "Уважаемый клиент, сформированы документы по кредитной заявке №";

        kafkaListenerService.sendSendDocumentsRequest(messageJson);

        verify(objectMapper, times(1)).readValue(messageJson, EmailMessage.class);
        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

    @Test
    void sendSendSesRequest() throws JsonProcessingException {
        String text = "Уважаемый клиент, подпишите с помощью SES кода документы по кредитной заявке №";

        kafkaListenerService.sendSendSesRequest(messageJson);

        verify(objectMapper, times(1)).readValue(messageJson, EmailMessage.class);
        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

    @Test
    void sendCreditIssueRequest() throws JsonProcessingException {
        String text = "Уважаемый клиент, Вам выдан кредит по заявке №";

        kafkaListenerService.sendCreditIssueRequest(messageJson);

        verify(objectMapper, times(1)).readValue(messageJson, EmailMessage.class);
        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

    @Test
    void sendApplicationDeniedMessage() throws JsonProcessingException {
        String text = "Уважаемый клиент, Вам отказано в выдаче кредита по заявке №";

        kafkaListenerService.sendApplicationDeniedMessage(messageJson);

        verify(objectMapper, times(1)).readValue(messageJson, EmailMessage.class);
        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

}