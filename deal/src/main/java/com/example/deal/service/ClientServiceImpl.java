package com.example.deal.service;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.Passport;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.interfaces.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public ClientEntity saveClient(LoanApplicationRequestDTO loanApplicationRequestDTO) {

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
        return clientRepository.save(client);
    }


}
