package com.example.deal.model;

import com.example.deal.model.enums.Gender;
import com.example.deal.model.enums.MaritalStatus;
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
@Table(name = "client")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long ClientId;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private Passport passport;
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private EmploymentDTO employment;
    private String account;

}
