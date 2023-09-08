package com.example.deal.util;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ApplicationStatusHistoryDTO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;

@UtilityClass
@Slf4j
public class ApplicationStatusUpdater {
    public static ApplicationEntity updateStatus(ApplicationEntity application, ApplicationStatusHistoryDTO.StatusEnum status) {
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
