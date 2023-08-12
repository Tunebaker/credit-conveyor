package com.example.deal.model;

import com.example.deal.model.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistory {

    private String status;
    private LocalDate time;
    private ChangeType changeType;
}
