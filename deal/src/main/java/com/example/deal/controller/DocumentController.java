package com.example.deal.controller;

import com.example.deal.api.DocumentsApi;
import com.example.deal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DocumentController implements DocumentsApi {

    private final DocumentService documentService;

    @Override
    public ResponseEntity<Void> sendDocuments(Long applicationId) {
        documentService.sendSendDocumentRequest(applicationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> signDocuments(Long applicationId) {
        documentService.sendSignDocumentRequest(applicationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> verifySesCode(Long applicationId, Integer sesCode) {
        documentService.sendCreditIssueRequest(applicationId, sesCode);
        return ResponseEntity.ok().build();
    }
}
