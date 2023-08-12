package com.example.deal.service;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.service.interfaces.ApplicationService;
import com.example.deal.service.interfaces.ClientService;
import com.example.deal.service.interfaces.DealService;
import com.example.deal.util.FeignServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DealServiceImpl implements DealService {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final FeignServiceUtil feignServiceUtil;

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        ClientEntity client = clientService.saveClient(loanApplicationRequestDTO);
        applicationService.saveApplication(client.getClientId());
        return feignServiceUtil.getLoanOfferDtos(loanApplicationRequestDTO);
    }

    @Override
    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        //////////
    }

    @Override
    public void calculateCredit(FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        //////////
    }
}
