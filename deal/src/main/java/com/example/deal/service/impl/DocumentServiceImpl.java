package com.example.deal.service.impl;

import com.example.deal.config.KafkaConfig;
import com.example.deal.exception.SesCodeException;
import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ClientEntity;
import com.example.deal.model.EmailMessage;
import com.example.deal.model.Theme;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.DocumentService;
import com.example.deal.service.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CREDIT_ISSUED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.DOCUMENT_SIGNED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREPARE_DOCUMENTS;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final KafkaService kafkaService;
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;
    private final KafkaConfig kafkaConfig;
    private String topic;

    @Override
    public void sendFinishRegistrationRequest(EmailMessage message) {
        topic = kafkaConfig.getFinishRegistrationTopic();
        kafkaService.send(getMessageJson(message), topic);
        log.info("Сообщение EmailMessage сериализовано для отправки в Kafka: {}", message);
    }

    @Override
    public void sendCreateDocumentRequest(EmailMessage message) {
        topic = kafkaConfig.getCreateDocumentsTopic();
        kafkaService.send(getMessageJson(message), topic);
        log.info("Сообщение EmailMessage сериализовано для отправки в Kafka: {}", message);
    }

    @Override
    public void sendSendDocumentRequest(Long applicationId) {
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        ApplicationEntity updatedApplication = applicationRepository.updateStatus(application, PREPARE_DOCUMENTS);
        log.info("Статус заявки  id={} изменен на: {}", applicationId, updatedApplication.getStatus());

        applicationRepository.save(updatedApplication);
        log.info("Обновлённая заявка сохранена в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = EmailMessage.builder()
                .theme(Theme.SEND_DOCUMENTS)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();

        topic = kafkaConfig.getSendDocumentsTopic();
        kafkaService.send(getMessageJson(message), topic);
        log.info("Сообщение EmailMessage сериализовано для отправки в Kafka: {}", message);
    }

    @Override
    public void sendSignDocumentRequest(Long applicationId) {

        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        int sesCode = getRandomSesCode();
        application.setSesCode(sesCode);
        log.info("Для заявки id={} сгенерирован код ПЭП: {}", applicationId, sesCode);
        applicationRepository.save(application);
        log.info("Обновлённая заявка сохранена в БД");
        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();

        EmailMessage message = EmailMessage.builder()
                .theme(Theme.SEND_SES)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();

        topic = kafkaConfig.getSendSesTopic();
        kafkaService.send(getMessageJson(message), topic);
        log.info("Сообщение EmailMessage сериализовано для отправки в Kafka: {}", message);
    }

    @Override
    public void sendCreditIssueRequest(Long applicationId, Integer sesCode) {
        log.info("От клиента получен ses-code: {}", sesCode);
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        if (!application.getSesCode().equals(sesCode)) {
            throw new SesCodeException("Неверный код ПЭП");
        }

        applicationRepository.updateStatus(application, DOCUMENT_SIGNED);
        applicationRepository.save(application);
        log.info("Обновлённая заявка сохранена в БД");

        applicationRepository.updateStatus(application, CREDIT_ISSUED);
        applicationRepository.save(application);
        log.info("Обновлённая заявка сохранена в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = EmailMessage.builder()
                .theme(Theme.CREDIT_ISSUED)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();
        topic = kafkaConfig.getCreditIssuedTopic();
        kafkaService.send(getMessageJson(message), topic);
        log.info("Сообщение EmailMessage сериализовано для отправки в Kafka: {}", message);
    }

    @Override
    public void sendApplicationDeniedRequest(EmailMessage message) {
        topic = kafkaConfig.getApplicationDeniedTopic();
        kafkaService.send(getMessageJson(message), topic);
        log.info("Сообщение EmailMessage сериализовано для отправки в Kafka: {}", message);
    }

    private String getMessageJson(EmailMessage message) {
        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.warn("Ошибка преобразования объекта EmailMessage в json: {}", message);
            throw new SesCodeException(e.getMessage());
        }
        return messageJson;
    }

    private Integer getRandomSesCode() {
        return new Random().nextInt(9000) + 1000;
    }
}
