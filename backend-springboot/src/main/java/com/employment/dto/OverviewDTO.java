package com.employment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewDTO {
    private Long totalRecords;
    private Long universityCount;
    private Long majorCount;
    private BigDecimal avgSalary;
    private BigDecimal avgEmploymentRate;
}
