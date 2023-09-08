package com.example.deal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "application")
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;
    private Long clientId;
    private Long creditId;
    @Enumerated(EnumType.STRING)
    private ApplicationStatusHistoryDTO.StatusEnum status;
    private LocalDateTime creationDate;
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private LoanOfferDTO appliedOffer;
    private LocalDateTime signDate;
    private Integer sesCode;
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private List<ApplicationStatusHistoryDTO> statusHistory;

}
