package com.example.application.service.impl;

import com.example.application.exception.PreScoringException;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.application.service.client.FeignDealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl {

    private final FeignDealService feignDealService;
    private final PreScoringServiceImpl preScoringService;

    private final static String PRESCORING_ERROR_MESSAGE = "заявка не прошла прескоринг по причинам: ";

    public List<LoanOfferDTO> createApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("прескоринг запроса: {}", loanApplicationRequestDTO);
        Map<String, String> validationErrors = preScoringService.preScore(loanApplicationRequestDTO);
        if (validationErrors.size() != 0) {
            log.warn(PRESCORING_ERROR_MESSAGE + "{}", validationErrors);
            throw new PreScoringException(PRESCORING_ERROR_MESSAGE + validationErrors);
        }
        log.info("запрос к МС deal с параметрами: {}", loanApplicationRequestDTO);
        List<LoanOfferDTO> offers = feignDealService.createApplication(loanApplicationRequestDTO);
        log.info("получен ответ от МС deal: {}", offers);
        return offers;
    }

    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        log.info("выбранное клиентом предложение направляется в МС deal: {}", loanOfferDTO);
        feignDealService.applyOffer(loanOfferDTO);
    }
}
