package com.example.deal.service;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.enums.ApplicationStatus;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.service.interfaces.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    @Override
    public ApplicationEntity saveApplication(Long clientId) {
        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(clientId)
                .status(ApplicationStatus.PREAPPROVAL)
                .build();
        return applicationRepository.save(application);
    }
}
