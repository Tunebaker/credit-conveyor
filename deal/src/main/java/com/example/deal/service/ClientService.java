package com.example.deal.service;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.LoanApplicationRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    List<ClientEntity> getAllClients();  /////// for testing purpose only /////////
    ClientEntity saveClient(LoanApplicationRequestDTO loanApplicationRequestDTO);
}
