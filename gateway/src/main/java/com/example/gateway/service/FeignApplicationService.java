package com.example.gateway.service;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(value = "feignApplication", url = "${application.url}")
public interface FeignApplicationService {

    @PostMapping("/application")
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);

    @PutMapping("application/offer")
    void applyOffer(LoanOfferDTO loanOfferDTO);
}
