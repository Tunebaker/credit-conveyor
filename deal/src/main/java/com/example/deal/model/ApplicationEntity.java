package com.example.deal.model;

import com.example.deal.model.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

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
    private ApplicationStatus status;
    private LocalDate creationDate;
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private LoanOfferDTO appliedOffer;
    private LocalDate signDate;
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private StatusHistory statusHistory;

}
