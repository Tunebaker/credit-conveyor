package com.example.gateway.service.impl;

import com.example.application.model.FinishRegistrationRequestDTO;
import com.example.application.model.LoanApplicationRequestDTO;
import com.example.application.model.LoanOfferDTO;
import com.example.gateway.service.FeignApplicationService;
import com.example.gateway.service.FeignDealService;
import com.example.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GatewayServiceImpl implements GatewayService {

    private final FeignApplicationService feignApplicationService;
    private final FeignDealService feignDealService;

    @Override
    public List<LoanOfferDTO> createLoanApplication(LoanApplicationRequestDTO dto) {
        log.info("От клиента получен первоначальный запрос на кредит: {}", dto);
        return feignApplicationService.createApplication(dto);
    }

    @Override
    public void chooseOneOffer(LoanOfferDTO dto) {
        log.info("От клиента получено выбранное кредитное предложение: {}", dto);
        feignApplicationService.applyOffer(dto);
    }

    @Override
    public void finishRegistration(Long id, FinishRegistrationRequestDTO dto) {
        log.info("Получено значение applicationId: {},\nFinishRegistrationRequestDTO: {}", id, dto);
        feignDealService.calculateCredit(id, dto);
    }

    @Override
    public void createDocumentRequest(Long id) {
        log.info("Получено значение applicationId: {}", id);
        feignDealService.sendDocuments(id);
    }

    @Override
    public void signDocumentRequest(Long id) {
        log.info("Получено значение applicationId: {}", id);
        feignDealService.signDocuments(id);
    }

    @Override
    public void verifySesCodeRequest(Long id, Integer sesCode) {
        log.info("Получено значение applicationId: {}, sesCode: {}", id, sesCode);
        feignDealService.verifySesCode(id, sesCode);
    }
}
