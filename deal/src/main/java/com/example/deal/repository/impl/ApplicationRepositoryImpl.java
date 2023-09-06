package com.example.deal.repository.impl;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ApplicationStatusHistoryDTO;
import com.example.deal.repository.ApplicationRepository;
import com.example.deal.repository.ApplicationRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;

@Repository
@Slf4j
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {
//    @Autowired
//    @Lazy
//    ApplicationRepository applicationRepository;

    public ApplicationEntity updateStatus(ApplicationEntity application, ApplicationStatusHistoryDTO.StatusEnum status) {
        log.info("Для заявки запрошено изменение статуса на: {} ", status);
        if (application.getStatusHistory() == null) {
            application.setStatusHistory(new ArrayList<>());
            log.info("Для новой заявки создана история статусов");
        }
        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = ApplicationStatusHistoryDTO.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(AUTOMATIC)
                .build();
        statusHistory.add(applicationStatusHistoryDTO);
        application.setStatusHistory(statusHistory);
        application.setStatus(status);
        log.info("Статус заявки изменен: {}", status);
        return application;

    }

}
