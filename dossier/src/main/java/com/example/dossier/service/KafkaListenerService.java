package com.example.dossier.service;

import com.example.dossier.model.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final EmailSenderService emailSenderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "finish-registration")
    public void sendFinishRegistrationRequest(String messageJson) {
        log.info("В Kafka получено сообщение: {}", messageJson);
        EmailMessage emailMessage = getEmailMessageFromJson(messageJson);
        String text = "Уважаемый клиент, завершите Вашу регистрацию по заявке №" + emailMessage.getApplicationId();
        emailSenderService.sendEmail(emailMessage, text);
        log.info("Клиенту отправлено e-mail сообщение: {} с текстом: {}", emailMessage, text);
    }

    @KafkaListener(topics = "create-documents")
    public void sendCreateDocumentsRequest(String messageJson) {
        log.info("В Kafka получено сообщение: {}", messageJson);
        EmailMessage emailMessage = getEmailMessageFromJson(messageJson);
        String text = "Уважаемый клиент, отправьте запрос для того, чтобы перейти к оформлению документов по заявке №" + emailMessage.getApplicationId();
        emailSenderService.sendEmail(emailMessage, text);
        log.info("Клиенту отправлено e-mail сообщение: {} с текстом: {}", emailMessage, text);
    }

    @KafkaListener(topics = "send-documents")
    public void sendSendDocumentsRequest(String messageJson) {
        log.info("В Kafka получено сообщение: {}", messageJson);
        EmailMessage emailMessage = getEmailMessageFromJson(messageJson);
        String text = "Уважаемый клиент, сформированы документы по кредитной заявке №" + emailMessage.getApplicationId();
        emailSenderService.sendEmail(emailMessage, text);
        log.info("Клиенту отправлено e-mail сообщение: {} с текстом: {}", emailMessage, text);
    }

    @KafkaListener(topics = "send-ses")
    public void sendSendSesRequest(String messageJson) {
        log.info("В Kafka получено сообщение: {}", messageJson);
        EmailMessage emailMessage = getEmailMessageFromJson(messageJson);
        String text = "Уважаемый клиент, подпишите с помощью SES кода документы по кредитной заявке №" + emailMessage.getApplicationId();
        emailSenderService.sendEmail(emailMessage, text);
        log.info("Клиенту отправлено e-mail сообщение: {} с текстом: {}", emailMessage, text);
    }

    @KafkaListener(topics = "credit-issued")
    public void sendCreditIssueRequest(String messageJson) {
        log.info("В Kafka получено сообщение: {}", messageJson);
        EmailMessage emailMessage = getEmailMessageFromJson(messageJson);
        String text = "Уважаемый клиент, Вам выдан кредит по заявке №" + emailMessage.getApplicationId();
        emailSenderService.sendEmail(emailMessage, text);
        log.info("Клиенту отправлено e-mail сообщение: {} с текстом: {}", emailMessage, text);
    }

    @KafkaListener(topics = "application-denied")
    public void sendApplicationDeniedMessage(String messageJson) {
        log.info("В Kafka получено сообщение: {}", messageJson);
        EmailMessage emailMessage = getEmailMessageFromJson(messageJson);
        String text = "Уважаемый клиент, Вам отказано в выдаче кредита по заявке №" + emailMessage.getApplicationId();
        emailSenderService.sendEmail(emailMessage, text);
        log.info("Клиенту отправлено e-mail сообщение: {} с текстом: {}", emailMessage, text);
    }

    private EmailMessage getEmailMessageFromJson(String messageJson) {
        EmailMessage emailMessage;
        try {
            emailMessage = objectMapper.readValue(messageJson, EmailMessage.class);
        } catch (JsonProcessingException e) {
            log.warn("Ошибка парсинга в объект EmailMessage: {}", messageJson);
            throw new RuntimeException(e);
        }
        return emailMessage;
    }


}
