package com.example.deal.service.impl;

import com.example.deal.exception.SesCodeException;
import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ApplicationStatusHistoryDTO;
import com.example.deal.model.ClientEntity;
import com.example.deal.model.EmailMessage;
import com.example.deal.model.Theme;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CREDIT_ISSUED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.DOCUMENT_SIGNED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREPARE_DOCUMENTS;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void sendFinishRegistrationRequest(EmailMessage message) {
        kafkaTemplate.send("finish-registration", getMessageJson(message));
        log.info("сообщение EmailMessage сериализовано и отправлено в МС Досье: {}", message);
    }

    @Override
    public void sendCreateDocumentRequest(EmailMessage message) {
        kafkaTemplate.send("create-documents", getMessageJson(message));
        log.info("сообщение EmailMessage сериализовано и отправлено в МС Досье: {}", message);
    }

    @Override
    public void sendSendDocumentRequest(Long applicationId) {
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        ApplicationEntity updatedApplication = DealServiceImpl.updateStatus(application, PREPARE_DOCUMENTS);
        log.info("статус заявки  id={} изменен на: {}", applicationId, updatedApplication.getStatus());

        applicationRepository.save(updatedApplication);
        log.info("обновлённая заявка сохранена в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = EmailMessage.builder()
                .theme(Theme.SEND_DOCUMENTS)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();

        kafkaTemplate.send("send-documents", getMessageJson(message));
        log.info("сообщение EmailMessage сериализовано и отправлено в МС Досье: {}", message);
    }

    @Override
    public void sendSignDocumentRequest(Long applicationId) {

        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        int sesCode = getRandomSesCode();
        application.setSesCode(sesCode);
        log.info("для заявки id={} сгенерирован код ПЭП: {}", applicationId, sesCode);
        applicationRepository.save(application);
        log.info("обновлённая заявка сохранена в БД");
        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();

        EmailMessage message = EmailMessage.builder()
                .theme(Theme.SEND_SES)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();

        kafkaTemplate.send("send-ses", getMessageJson(message));
        log.info("сообщение EmailMessage сериализовано и отправлено в МС Досье: {}", message);
    }

    @Override
    public void sendCreditIssueRequest(Long applicationId, Integer sesCode) {
        log.info("от клиента получен ses-code: {}", sesCode);
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        if(!application.getSesCode().equals(sesCode)){
            throw new SesCodeException("неверный код ПЭП");
        }

        DealServiceImpl.updateStatus(application, DOCUMENT_SIGNED);
        applicationRepository.save(application);
        log.info("обновлённая заявка сохранена в БД");

        DealServiceImpl.updateStatus(application, CREDIT_ISSUED);
        applicationRepository.save(application);
        log.info("обновлённая заявка сохранена в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = EmailMessage.builder()
                .theme(Theme.CREDIT_ISSUED)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();
        kafkaTemplate.send("credit-issued", getMessageJson(message));
        log.info("сообщение EmailMessage сериализовано и отправлено в МС Досье: {}", message);
    }

    @Override
    public void sendApplicationDeniedRequest(EmailMessage message) {
        kafkaTemplate.send("application-denied", getMessageJson(message));
        log.info("сообщение EmailMessage сериализовано и отправлено в МС Досье: {}", message);
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

    private Integer getRandomSesCode(){
        return new Random().nextInt(9000) + 1000;
    }
}
