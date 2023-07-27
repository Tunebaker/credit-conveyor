package com.example.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmploymentDTO {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

    public enum EmploymentStatus{
        EMPLOYED,
        UNEMPLOYED,
        SELF_EMPLOYED,
        BUSINESS_OWNER
    }

    public enum Position{
        ORDINARY_WORKER,
        MIDDLE_MANAGER,
        TOP_MANAGER
    }
}


