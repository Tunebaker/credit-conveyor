package com.example.deal.service;

import com.example.deal.config.KafkaConfig;
import com.example.deal.exception.SesCodeException;
import com.example.deal.model.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaConfig kafkaConfig;
    private final ObjectMapper objectMapper;
    private String topic;

     public void sendFinishRegistrationRequest(EmailMessage message){
        topic = kafkaConfig.getFinishRegistrationTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, message);
    }

    public void sendCreateDocumentRequest(EmailMessage message){
        topic = kafkaConfig.getCreateDocumentsTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, message);
    }

    public void sendSendDocumentRequest(EmailMessage message){
        topic = kafkaConfig.getSendDocumentsTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, message);
    }

    public void sendSignDocumentRequest(EmailMessage message){
        topic = kafkaConfig.getSendSesTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, message);
    }

    public void sendCreditIssueRequest(EmailMessage message){
        topic = kafkaConfig.getCreditIssuedTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, message);
    }

    public void sendApplicationDeniedRequest(EmailMessage message){
        topic = kafkaConfig.getApplicationDeniedTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, message);
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
}
