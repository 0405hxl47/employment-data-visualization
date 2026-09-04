package com.employment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BoxPlotDTO {
    private String name;
    private BigDecimal min;
    private BigDecimal q1;
    private BigDecimal median;
    private BigDecimal q3;
    private BigDecimal max;
    private Integer count;

    public BoxPlotDTO() {}

    public BoxPlotDTO(String name, List<BigDecimal> sortedValues) {
        this.name = name;
        int n = sortedValues.size();
        this.min = sortedValues.get(0);
        this.max = sortedValues.get(n - 1);
        this.q1 = sortedValues.get(n / 4);
        this.median = sortedValues.get(n / 2);
        this.q3 = sortedValues.get(3 * n / 4);
        this.count = n;
    }
}
