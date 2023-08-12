package com.example.deal.service.interfaces;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.LoanApplicationRequestDTO;


public interface ClientService {
    ClientEntity saveClient(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
