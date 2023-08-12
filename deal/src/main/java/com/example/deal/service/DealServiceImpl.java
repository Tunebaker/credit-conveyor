package com.example.deal.service;

import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealServiceImpl implements DealService{
    ClientService clientService;

    @Override
    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        clientService.saveClient(loanApplicationRequestDTO);
        return null;
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
