package com.example.deal.service.impl;

import com.example.deal.exception.ScoringException;
import com.example.deal.mapper.ClientMapper;
import com.example.deal.mapper.CreditMapper;
import com.example.deal.mapper.ScoringDataDTOMapper;
import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ClientEntity;
import com.example.deal.model.CreditDTO;
import com.example.deal.model.CreditEntity;
import com.example.deal.model.EmailMessage;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.model.ScoringDataDTO;
import com.example.deal.model.Theme;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.DealService;
import com.example.deal.service.DocumentService;
import com.example.deal.service.client.FeignConveyorService;
import com.example.deal.util.ApplicationStatusUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CC_APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.CC_DENIED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREAPPROVAL;
import static com.example.deal.model.Theme.APPLICATION_DENIED;
import static com.example.deal.model.Theme.CREATE_DOCUMENTS;
import static com.example.deal.model.Theme.FINISH_REGISTRATION;

@Slf4j
@RequiredArgsConstructor
@Service
public class DealServiceImpl implements DealService {

    private static final String APP_SAVED_MSG = "Заявка сохранена в БД";
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
        ApplicationStatusUpdater.updateStatus(application, PREAPPROVAL);

        applicationRepository.save(application);

        log.info(APP_SAVED_MSG + ": {}", application);

        log.info("Запрос для расчета возможных условий кредита отправляется в МС Конвейер");
        List<LoanOfferDTO> loanOfferDtos = feignConveyorService.getLoanOfferDtos(loanApplicationRequestDTO);
        loanOfferDtos.forEach(offer -> offer.setApplicationId(application.getApplicationId()));
        log.info("Рассчитаны предложения по кредиту : {}", loanOfferDtos);
        return loanOfferDtos;
    }

    @Override
    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        log.info("Клиент выбрал предложение: {}", loanOfferDTO);
        ApplicationEntity application = applicationRepository.findById(loanOfferDTO.getApplicationId()).orElseThrow();

        ApplicationStatusUpdater.updateStatus(application, APPROVED);

        application.setAppliedOffer(loanOfferDTO);
        applicationRepository.save(application);
        log.info(APP_SAVED_MSG + ": {}", application);

        ClientEntity client = clientRepository.findById(application.getClientId()).orElseThrow();
        EmailMessage message = createEmailMessage(client.getEmail(), FINISH_REGISTRATION, application.getApplicationId());
        log.info("Сформирован запрос на отправку письма о необходимости завершения регистрации: {}", message);

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
        } catch (ScoringException e) {
            log.warn("В выдаче кредита отказано по причине(-ам): " + e.getMessage());

            EmailMessage message = createEmailMessage(client.getEmail(), APPLICATION_DENIED, applicationId);
            documentService.sendApplicationDeniedRequest(message);
            log.info("Сформирован запрос на отправку письма об отказе в выдаче кредита: {}", message);

            applicationRepository.save(ApplicationStatusUpdater.updateStatus(application, CC_DENIED));
            log.info("Статус заявки установлен: CC_DENIED");

            throw new ScoringException(e.getMessage());
        }

        log.info("Сформированный запрос для полного расчета кредита отправлен в МС Конвейер: {}", scoringDataDTO);

        CreditEntity credit = CreditMapper.INSTANCE.creditDTOToCredit(creditDTO);
        log.info("Рассчитаны параметры кредита: {}", credit);
        CreditEntity savedCredit = creditRepository.save(credit);
        log.info("Параметры кредита сохранены в БД: {}", savedCredit);

        application.setCreditId(savedCredit.getCreditId());
        application.setCreationDate(LocalDateTime.now());
        applicationRepository.save(ApplicationStatusUpdater.updateStatus(application, CC_APPROVED));
        log.info(APP_SAVED_MSG + ": {}", application);

        ClientEntity updatedClient = ClientMapper.INSTANCE.finishRegistrationRequestUpdateFields(client,
                finishRegistrationRequestDTO);
        clientRepository.save(updatedClient);
        log.info("Данные о клиенте обновлены в БД: {}", updatedClient);

        EmailMessage message = createEmailMessage(updatedClient.getEmail(), CREATE_DOCUMENTS, applicationId);
        documentService.sendCreateDocumentRequest(message);
        log.info("Сформирован запрос на отправку письма о запросе на создание документов: {}", message);

    }

    private EmailMessage createEmailMessage(String address, Theme theme, Long applicationId) {
        return EmailMessage.builder()
                .address(address)
                .theme(theme)
                .applicationId(applicationId)
                .build();
    }
}
