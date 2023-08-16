package com.example.deal.mapper;

import com.example.deal.model.ClientEntity;
import com.example.deal.model.FinishRegistrationRequestDTO;
import com.example.deal.model.LoanOfferDTO;
import com.example.deal.model.ScoringDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScoringDataDTOMapper {
    ScoringDataDTOMapper INSTANCE = Mappers.getMapper(ScoringDataDTOMapper.class);

    @Mapping(source = "requestedAmount", target = "amount")
    ScoringDataDTO loanOfferDtoToScoringDataDTO(LoanOfferDTO loanOfferDTO);

    @Mapping(source = "birthDate", target = "birthdate")
    @Mapping(source = "passport.series", target = "passportSeries")
    @Mapping(source = "passport.number", target = "passportNumber")
    @Mapping(source = "passport.issueDate", target = "passportIssueDate")
    @Mapping(source = "passport.issueBranch", target = "passportIssueBranch")
    ScoringDataDTO clientEntityToScoringDataDTOUpdate(@MappingTarget ScoringDataDTO scoringDataDTO,
                                                      ClientEntity clientEntity);

    ScoringDataDTO finishRegistrationRequestToScoringDataUpdate(@MappingTarget ScoringDataDTO scoringDataDTO,
                                                                FinishRegistrationRequestDTO finishRegistrationRequestDTO);
}
