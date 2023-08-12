package com.example.deal.service;

import com.example.deal.model.*;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.interfaces.DealService;
import com.example.deal.util.FeignServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.APPROVED;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREAPPROVAL;

@RequiredArgsConstructor
@Service
public class DealServiceImpl implements DealService {

    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final FeignServiceUtil feignServiceUtil;

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Passport passport = Passport.builder()
                .series(loanApplicationRequestDTO.getPassportSeries())
                .number(loanApplicationRequestDTO.getPassportNumber())
                .build();

        ClientEntity client = ClientEntity.builder()
                .lastName(loanApplicationRequestDTO.getLastName())
                .firstName(loanApplicationRequestDTO.getFirstName())
                .middleName(loanApplicationRequestDTO.getMiddleName())
                .birthDate(loanApplicationRequestDTO.getBirthdate())
                .email(loanApplicationRequestDTO.getEmail())
                .passport(passport)
                .build();
        client = clientRepository.save(client);


        List<ApplicationStatusHistoryDTO> history = new ArrayList<>();
        history.add(ApplicationStatusHistoryDTO.builder()
                .status(PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC)
                .build());

        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(client.getClientId())
                .status(PREAPPROVAL)
                .statusHistory(history)
                .build();
        applicationRepository.save(application);


        List<LoanOfferDTO> loanOfferDtos = feignServiceUtil.getLoanOfferDtos(loanApplicationRequestDTO);
        loanOfferDtos.forEach(offer -> offer.setApplicationId(application.getApplicationId()));
        return loanOfferDtos;
    }

    @Override
    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        ApplicationEntity application = applicationRepository.findById(loanOfferDTO.getApplicationId()).orElseThrow();
        List<ApplicationStatusHistoryDTO> applicationStatusHistoryDTOs = application.getStatusHistory();
        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = ApplicationStatusHistoryDTO.builder()
                .status(APPROVED)
                .time(LocalDateTime.now())
                .changeType(AUTOMATIC)
                .build();

        applicationStatusHistoryDTOs.add(applicationStatusHistoryDTO);
        application.setStatus(APPROVED);
        application.setAppliedOffer(loanOfferDTO);
        application.setStatusHistory(applicationStatusHistoryDTOs);
        applicationRepository.save(application);
    }

    @Override
    public void calculateCredit(FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        //////////
    }
}
