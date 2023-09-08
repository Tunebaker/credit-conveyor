package com.example.application.service.impl;

import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.application.service.ApplicationService;
import com.example.application.service.client.FeignDealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final FeignDealService feignDealService;
    private final PreScoringServiceImpl preScoringService;

    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("Прескоринг запроса: {}", loanApplicationRequestDTO);
        preScoringService.preScore(loanApplicationRequestDTO);

        log.info("Запрос к МС deal с параметрами: {}", loanApplicationRequestDTO);
        List<LoanOfferDTO> offers = feignDealService.createApplication(loanApplicationRequestDTO);
        log.info("Получен ответ от МС deal: {}", offers);
        return offers;
    }

    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        log.info("Выбранное клиентом предложение направляется в МС deal: {}", loanOfferDTO);
        feignDealService.applyOffer(loanOfferDTO);
    }
}
