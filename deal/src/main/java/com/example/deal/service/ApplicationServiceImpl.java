package com.example.deal.service;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ApplicationStatusHistoryDTO;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.service.interfaces.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.StatusEnum.PREAPPROVAL;

@RequiredArgsConstructor
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Override
    public ApplicationEntity saveApplication(Long clientId) {
        List<ApplicationStatusHistoryDTO> history = new ArrayList<>();
        history.add(ApplicationStatusHistoryDTO.builder()
                .status(PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC)
                .build());

        ApplicationEntity application = ApplicationEntity.builder()
                .clientId(clientId)
                .status(PREAPPROVAL)
                .statusHistory(history)
                .build();
        return applicationRepository.save(application);
    }
}
