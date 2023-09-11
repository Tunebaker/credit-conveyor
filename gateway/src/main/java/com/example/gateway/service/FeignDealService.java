package com.example.gateway.service;

import com.example.application.model.FinishRegistrationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "feignDeal", url = "${deal.url}")
public interface FeignDealService {

    @PutMapping("/deal/calculate/{applicationId}")
    void calculateCredit(Long applicationId, FinishRegistrationRequestDTO dto);

    @PutMapping("/deal/document/{applicationId}/send")
    void sendDocuments(@PathVariable Long applicationId);

    @PutMapping("/deal/document/{applicationId}/sign")
    void signDocuments(@PathVariable Long applicationId);

    @PutMapping("/deal/document/{applicationId}/code")
    void verifySesCode(@RequestHeader Integer sesCode, @PathVariable Long applicationId);
}

