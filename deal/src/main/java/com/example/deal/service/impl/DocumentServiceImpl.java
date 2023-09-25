package com.example.deal.service.impl;

import com.example.deal.exception.SesCodeException;
import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ClientEntity;
import com.example.deal.model.CreditEntity;
import com.example.deal.model.CreditStatus;
import com.example.deal.model.EmailMessage;
import com.example.deal.model.Theme;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.DocumentService;
import com.example.deal.service.KafkaService;
import com.example.deal.util.ApplicationStatusUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CREDIT_ISSUED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.DOCUMENT_SIGNED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREPARE_DOCUMENTS;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final String MESSAGE_SENT_MSG = "Сообщение отправлено в KafkaService: {}";
    private final SecureRandom random = new SecureRandom();
    private final KafkaService kafkaService;
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;

    @Override
    public void sendFinishRegistrationRequest(EmailMessage message) {
        kafkaService.sendFinishRegistrationRequest(message);

        log.info(MESSAGE_SENT_MSG, message);
    }

    @Override
    public void sendCreateDocumentRequest(EmailMessage message) {
        kafkaService.sendCreateDocumentRequest(message);
        log.info(MESSAGE_SENT_MSG, message);
    }

    @Override
    public void sendSendDocumentRequest(Long applicationId) {
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        ApplicationEntity updatedApplication = ApplicationStatusUpdater.updateStatus(application, PREPARE_DOCUMENTS);
        log.info("Статус заявки  id={} изменен на: {}", applicationId, updatedApplication.getStatus());

        applicationRepository.save(updatedApplication);
        log.info("Обновлённая заявка сохранена в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = createEmailMessage(Theme.SEND_DOCUMENTS, applicationId, client);

        kafkaService.sendSendDocumentRequest(message);
        log.info(MESSAGE_SENT_MSG, message);
    }

    @Override
    public void sendSignDocumentRequest(Long applicationId) {

        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        Integer sesCode = getRandomSesCode();
        application.setSesCode(sesCode);
        log.info("Для заявки id={} сгенерирован код ПЭП: {}", applicationId, sesCode);

        applicationRepository.save(application);
        log.info("Обновлённая заявка сохранена в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = createEmailMessage(Theme.SEND_SES, applicationId, client);

        kafkaService.sendSignDocumentRequest(message);
        log.info(MESSAGE_SENT_MSG, message);
    }

    @Override
    public void sendCreditIssueRequest(Long applicationId, Integer sesCode) {
        log.info("От клиента получен ses-code: {}", sesCode);
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        if (!application.getSesCode().equals(sesCode)) {
            throw new SesCodeException("Неверный код ПЭП");
        }

        ApplicationStatusUpdater.updateStatus(application, DOCUMENT_SIGNED);
        applicationRepository.save(application);
        log.info("Обновлённая заявка сохранена в БД");

        ApplicationStatusUpdater.updateStatus(application, CREDIT_ISSUED);
        applicationRepository.save(application);
        log.info("Обновлённая заявка сохранена в БД");

        CreditEntity credit = creditRepository.findById(application.getCreditId()).orElseThrow();
        credit.setCreditStatus(CreditStatus.ISSUED);
        log.info("Для кредита id: {} установлен статус ISSUED", credit.getCreditId());
        creditRepository.save(credit);
        log.info("Обновлённый кредит сохранен в БД");

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = createEmailMessage(Theme.CREDIT_ISSUED, applicationId, client);
        kafkaService.sendCreditIssueRequest(message);
        log.info(MESSAGE_SENT_MSG, message);
    }

    @Override
    public void sendApplicationDeniedRequest(EmailMessage message) {
        kafkaService.sendApplicationDeniedRequest(message);
        log.info(MESSAGE_SENT_MSG, message);
    }

    private EmailMessage createEmailMessage(Theme theme, Long applicationId, ClientEntity client) {
        return EmailMessage.builder()
                .theme(theme)
                .applicationId(applicationId)
                .address(client.getEmail())
                .build();
    }

    private Integer getRandomSesCode() {
        return random.nextInt(9000) + 1000;
    }
}
