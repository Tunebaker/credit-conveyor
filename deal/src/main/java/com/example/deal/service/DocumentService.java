package com.example.deal.service;

import com.example.deal.model.EmailMessage;

public interface DocumentService {
    void sendFinishRegistrationRequest(EmailMessage emailMessage);

    void sendCreateDocumentRequest(EmailMessage emailMessage);

    void sendSendDocumentRequest(Long applicationId);

    void sendSignDocumentRequest(Long applicationId);

    void sendCreditIssueRequest(Long applicationId, Integer sesCode);

    void sendApplicationDeniedRequest(EmailMessage emailMessage);
}
