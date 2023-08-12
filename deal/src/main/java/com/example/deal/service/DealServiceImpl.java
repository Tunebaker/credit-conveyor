package com.example.deal.service;

import com.example.deal.model.*;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.service.interfaces.ApplicationService;
import com.example.deal.service.interfaces.ClientService;
import com.example.deal.service.interfaces.DealService;
import com.example.deal.util.FeignServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;
import static com.example.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL;
import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.APPROVED;

@RequiredArgsConstructor
@Service
public class DealServiceImpl implements DealService {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;
    private final FeignServiceUtil feignServiceUtil;

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        ClientEntity client = clientService.saveClient(loanApplicationRequestDTO);
        ApplicationEntity application = applicationService.saveApplication(client.getClientId());
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
