package com.example.deal.mapper;

import com.example.deal.model.CreditDTO;
import com.example.deal.model.CreditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreditMapper {
    CreditMapper INSTANCE = Mappers.getMapper(CreditMapper.class);

    @Mapping(target = "creditStatus", constant = "CALCULATED")
    @Mapping(source = "isSalaryClient", target = "salaryClient")
    @Mapping(source = "isInsuranceEnabled", target = "insuranceEnable")
    CreditEntity creditDTOToCredit(CreditDTO creditDTO);
}
