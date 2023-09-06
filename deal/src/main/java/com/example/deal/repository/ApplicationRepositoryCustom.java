package com.example.deal.repository;

import com.example.deal.model.ApplicationEntity;
import com.example.deal.model.ApplicationStatusHistoryDTO;

public interface ApplicationRepositoryCustom {

    ApplicationEntity updateStatus(ApplicationEntity application, ApplicationStatusHistoryDTO.StatusEnum status);
}
