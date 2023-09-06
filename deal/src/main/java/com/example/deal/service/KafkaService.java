package com.example.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String messageJson, String topic){
        kafkaTemplate.send(topic, messageJson);
        log.info("Сообщение c темой: {}\r\n" +
                "отправлено в МС Досье: {}", topic, messageJson);
    }
}
