package com.example.deal.service;

import com.example.deal.model.*;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.enums.Gender;
import com.example.deal.model.enums.MaritalStatus;
import com.example.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public List<ClientEntity> getAllClients() {
        return (List<ClientEntity>) clientRepository.findAll();
    }

    @Override
    public ClientEntity saveClient(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Passport passport = Passport.builder()
                .series(loanApplicationRequestDTO.getPassportSeries())
                .number(loanApplicationRequestDTO.getPassportNumber())
                .build();

        return clientRepository.save(ClientEntity.builder()
                .lastName(loanApplicationRequestDTO.getLastName())
                .firstName(loanApplicationRequestDTO.getFirstName())
                .middleName(loanApplicationRequestDTO.getMiddleName())
                .birthDate(loanApplicationRequestDTO.getBirthdate())
                .email(loanApplicationRequestDTO.getEmail())
                .passport(passport)
                .build());
    }


}
