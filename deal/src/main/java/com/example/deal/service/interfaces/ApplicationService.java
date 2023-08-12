package com.example.deal.service.interfaces;

import com.example.deal.model.ApplicationEntity;

public interface ApplicationService {
    ApplicationEntity saveApplication(Long clientId);
    ApplicationEntity findApplicationById(Long id);
}
