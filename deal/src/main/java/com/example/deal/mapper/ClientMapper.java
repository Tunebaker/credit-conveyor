package com.example.deal.mapper;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.LoanApplicationRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(source = "birthdate", target = "birthDate")
    @Mapping(source = "passportSeries", target = "passport.series")
    @Mapping(source = "passportNumber", target = "passport.number")

    ClientEntity loanApplicationRequestToClient(LoanApplicationRequestDTO requestDTO);
}
