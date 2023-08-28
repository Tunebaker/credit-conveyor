package com.example.application.service.client;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(value = "feignDeal", url = "${deal.url}")
public interface FeignDealService {
    @PostMapping("/deal/application")
    List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO);

    @PutMapping("/deal/offer")
    void applyOffer(LoanOfferDTO loanOfferDTO);
}
