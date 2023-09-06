package com.example.deal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("kafka")
@Data
public class KafkaConfig {
    private String finishRegistrationTopic;
    private String createDocumentsTopic;
    private String sendDocumentsTopic;
    private String sendSesTopic;
    private String creditIssuedTopic;
    private String applicationDeniedTopic;

}