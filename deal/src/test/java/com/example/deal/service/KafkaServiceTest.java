package com.example.deal.service;

import com.example.deal.config.KafkaConfig;
import com.example.deal.model.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @InjectMocks
    KafkaService kafkaService;
    @Mock
    KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    KafkaConfig kafkaConfig;
    @Mock
    ObjectMapper objectMapper;
    String messageJson;
    String topic;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        messageJson = "string string";
        when(objectMapper.writeValueAsString(any(EmailMessage.class))).thenReturn(messageJson);

    }

    @Test
    void sendFinishRegistrationRequest() {
        topic = "finish-registration";
        when(kafkaConfig.getFinishRegistrationTopic()).thenReturn(topic);

        kafkaService.sendFinishRegistrationRequest(new EmailMessage());

        verify(kafkaConfig, times(1)).getFinishRegistrationTopic();
        verify(kafkaTemplate, times(1)).send(topic, messageJson);
    }

    @Test
    void sendCreateDocumentRequest() {
        topic = "create-documents";
        when(kafkaConfig.getCreateDocumentsTopic()).thenReturn(topic);

        kafkaService.sendCreateDocumentRequest(new EmailMessage());

        verify(kafkaConfig, times(1)).getCreateDocumentsTopic();
        verify(kafkaTemplate, times(1)).send(topic, messageJson);
    }

    @Test
    void sendSendDocumentRequest() {
        topic = "create-documents";
        when(kafkaConfig.getSendDocumentsTopic()).thenReturn(topic);

        kafkaService.sendSendDocumentRequest(new EmailMessage());

        verify(kafkaConfig, times(1)).getSendDocumentsTopic();
        verify(kafkaTemplate, times(1)).send(topic, messageJson);
    }

    @Test
    void sendSignDocumentRequest() {
        topic = "sign-documents";
        when(kafkaConfig.getSendSesTopic()).thenReturn(topic);

        kafkaService.sendSignDocumentRequest(new EmailMessage());

        verify(kafkaConfig, times(1)).getSendSesTopic();
        verify(kafkaTemplate, times(1)).send(topic, messageJson);
    }

    @Test
    void sendCreditIssueRequest() {
        topic = "credit-issued";
        when(kafkaConfig.getCreditIssuedTopic()).thenReturn(topic);

        kafkaService.sendCreditIssueRequest(new EmailMessage());

        verify(kafkaConfig, times(1)).getCreditIssuedTopic();
        verify(kafkaTemplate, times(1)).send(topic, messageJson);
    }

    @Test
    void sendApplicationDeniedRequest() {
        topic = "application-denied";
        when(kafkaConfig.getApplicationDeniedTopic()).thenReturn(topic);

        kafkaService.sendApplicationDeniedRequest(new EmailMessage());

        verify(kafkaConfig, times(1)).getApplicationDeniedTopic();
        verify(kafkaTemplate, times(1)).send(topic, messageJson);
    }
}