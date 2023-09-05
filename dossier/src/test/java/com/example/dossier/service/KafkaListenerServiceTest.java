package com.example.dossier.service;

import com.example.dossier.model.EmailMessage;
import com.example.dossier.model.Theme;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    @Mock
    EmailSenderService emailSenderService;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    KafkaListenerService kafkaListenerService;
    EmailMessage emailMessage;
    String messageJson;
    Long appId;

    @BeforeEach
    void init() {
        appId = 2L;
        emailMessage = new EmailMessage("alandrr@ya.ru",
                Theme.CREDIT_ISSUED, 2L);
        messageJson = "";
        when(kafkaListenerService.getEmailMessageFromJson(messageJson)).thenReturn(emailMessage);
    }

    @Test
    void sendFinishRegistrationRequest() {
        String text = "Уважаемый клиент, завершите Вашу регистрацию по заявке №";

        kafkaListenerService.sendFinishRegistrationRequest(messageJson);

        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);
    }

    @Test
    void sendCreateDocumentsRequest() {
        String text = "Уважаемый клиент, отправьте запрос для того, чтобы перейти к оформлению документов по заявке №";

        kafkaListenerService.sendCreateDocumentsRequest(messageJson);

        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);
    }

    @Test
    void sendSendDocumentsRequest() {
        String text = "Уважаемый клиент, сформированы документы по кредитной заявке №";

        kafkaListenerService.sendSendDocumentsRequest(messageJson);

        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

    @Test
    void sendSendSesRequest() {
        String text = "Уважаемый клиент, подпишите с помощью SES кода документы по кредитной заявке №";

        kafkaListenerService.sendSendSesRequest(messageJson);

        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

    @Test
    void sendCreditIssueRequest() {
        String text = "Уважаемый клиент, Вам выдан кредит по заявке №";

        kafkaListenerService.sendCreditIssueRequest(messageJson);

        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

    @Test
    void sendApplicationDeniedMessage() {
        String text = "Уважаемый клиент, Вам отказано в выдаче кредита по заявке №";

        kafkaListenerService.sendApplicationDeniedMessage(messageJson);

        verify(emailSenderService, times(1)).sendEmail(emailMessage, text + appId);

    }

}