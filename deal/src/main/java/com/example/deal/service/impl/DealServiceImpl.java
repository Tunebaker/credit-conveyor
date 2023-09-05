package com.example.deal.service.impl;

import com.example.deal.mapper.ClientMapper;
import com.example.deal.mapper.CreditMapper;
import com.example.deal.mapper.ScoringDataDTOMapper;
import com.example.deal.model.*;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.DealService;
import com.example.deal.service.DocumentService;
import com.example.deal.service.client.FeignConveyorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CC_APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CC_DENIED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREAPPROVAL;

@Slf4j
@RequiredArgsConstructor
@Service
public class DealServiceImpl implements DealService {

    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final FeignConveyorService feignConveyorService;
    private final DocumentService documentService;

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("Получен запрос на расчёт возможных условий кредита: {}", loanApplicationRequestDTO);
        ClientEntity client = ClientMapper.INSTANCE.loanApplicationRequestToClient(loanApplicationRequestDTO);
        client = clientRepository.save(client);
        log.info("Данные клиента сохранены в БД: {}", client);

        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(client.getClientId())
                .build();
        updateStatus(application, PREAPPROVAL);

        applicationRepository.save(application);
        log.info("Заявка сохранена в БД: {}", application);

        log.info("Запрос для расчета возможных условий кредита отправляется в МС Конвейер");
        List<LoanOfferDTO> loanOfferDtos = feignConveyorService.getLoanOfferDtos(loanApplicationRequestDTO);
        loanOfferDtos.forEach(offer -> offer.setApplicationId(application.getApplicationId()));
        log.info("Рассчитаны предложения по кредиту : {}", loanOfferDtos);
        return loanOfferDtos;
    }

    @Override
    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        log.info("Клиент выбрал предложение {}", loanOfferDTO);
        ApplicationEntity application = applicationRepository.findById(loanOfferDTO.getApplicationId()).orElseThrow();

        updateStatus(application, APPROVED);

        application.setAppliedOffer(loanOfferDTO);
        applicationRepository.save(application);
        log.info("Заявка сохранена в БД: {}", application);

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        String email = client.getEmail();
        EmailMessage message = EmailMessage.builder()
                .address(email)
                .theme(Theme.FINISH_REGISTRATION)
                .applicationId(application.getApplicationId())
                .build();
        log.info("сформирован запрос на отправку письма о необходимости завершения регистрации: {}", message);

        documentService.sendFinishRegistrationRequest(message);
    }

    @Override
    public void calculateCredit(FinishRegistrationRequestDTO finishRegistrationRequestDTO, Long applicationId) {
        log.info("По заявке id = {} получены данные для окончательного расчёта {}", applicationId, finishRegistrationRequestDTO);
        ApplicationEntity application = applicationRepository.findById(applicationId).orElseThrow();
        LoanOfferDTO appliedOffer = application.getAppliedOffer();
        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();

        ScoringDataDTOMapper mapper = ScoringDataDTOMapper.INSTANCE;
        ScoringDataDTO scoringDataDTO = mapper.loanOfferDtoToScoringDataDTO(appliedOffer);
        scoringDataDTO = mapper.clientEntityToScoringDataDTOUpdate(scoringDataDTO, client);
        scoringDataDTO = mapper.finishRegistrationRequestToScoringDataUpdate(scoringDataDTO, finishRegistrationRequestDTO);

        CreditDTO creditDTO;

        try {
            creditDTO = feignConveyorService.calculateCredit(scoringDataDTO);
        } catch (Exception e) {
            log.warn("В выдаче кредита отказано по причине(-ам): " + e.getMessage() );

            EmailMessage message = EmailMessage.builder()
                    .applicationId(applicationId)
                    .theme(Theme.APPLICATION_DENIED)
                    .address(client.getEmail())
                    .build();
            documentService.sendApplicationDeniedRequest(message);
            log.info("сформирован запрос на отправку письма об отказе в выдаче кредита: {}", message);

            applicationRepository.save(updateStatus(application, CC_DENIED));
            log.info("Статус заявки установлен в CC_DENIED");

            throw new RuntimeException(e);
        }

        log.info("Сформированный запрос для полного расчета кредита отправлен в МС Конвейер {}", scoringDataDTO);

        CreditEntity credit = CreditMapper.INSTANCE.creditDTOToCredit(creditDTO);
        log.info("Рассчитаны параметры кредита: {}", credit);
        CreditEntity savedCredit = creditRepository.save(credit);
        log.info("Параметры кредита сохранены в БД: {}", savedCredit);

        application.setCreditId(savedCredit.getCreditId());
        application.setCreationDate(LocalDateTime.now());
        applicationRepository.save(updateStatus(application, CC_APPROVED));
        log.info("Заявка сохранена в БД: {}", application);

        ClientEntity updatedClient = ClientMapper.INSTANCE.finishRegistrationRequestUpdateFields(client,
                finishRegistrationRequestDTO);
        clientRepository.save(updatedClient);
        log.info("Данные о клиенте обновлены в БД: {}", updatedClient);

        EmailMessage message = EmailMessage.builder()
                .address(updatedClient.getEmail())
                .theme(Theme.CREATE_DOCUMENTS)
                .applicationId(applicationId)
                .build();
        documentService.sendCreateDocumentRequest(message);
        log.info("сформирован запрос на отправку письма о запросе на создание документов: {}", message);

    }

    public static ApplicationEntity updateStatus(ApplicationEntity application, ApplicationStatusHistoryDTO.StatusEnum status) {
        log.info("Для заявки запрошено изменение статуса на {} ", status);
        if (application.getStatusHistory() == null) {
            application.setStatusHistory(new ArrayList<>());
            log.info("Для новой заявки создана история статусов");
        }
        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = ApplicationStatusHistoryDTO.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(AUTOMATIC)
                .build();
        statusHistory.add(applicationStatusHistoryDTO);
        application.setStatusHistory(statusHistory);
        application.setStatus(status);
        log.info("Статус заявки изменен: {}", status);
        return application;
    }
}
