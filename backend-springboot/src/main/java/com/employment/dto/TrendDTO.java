package com.employment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrendDTO {
    private Integer graduationYear;
    private BigDecimal avgSalary;
    private BigDecimal avgRate;
    private Long recordCount;
}
