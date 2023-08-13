package com.example.deal.util;

import com.example.deal.model.CreditDTO;
import com.example.deal.model.LoanApplicationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.model.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "feignConveyor", url = "http://localhost:8080/conveyor")
public interface FeignServiceUtil {

    @PostMapping("/offers")
    List<LoanOfferDTO> getLoanOfferDtos(LoanApplicationRequestDTO loanApplicationRequestDTO);

    @PostMapping("/calculation")
    CreditDTO calculateCredit(ScoringDataDTO scoringDataDTO);
}
