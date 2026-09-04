package com.employment.dto;

import lombok.Data;

import java.util.List;

@Data
public class SunburstNodeDTO {
    private String name;
    private Long value;
    private List<SunburstNodeDTO> children;

    public SunburstNodeDTO() {}

    public SunburstNodeDTO(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public SunburstNodeDTO(String name, List<SunburstNodeDTO> children) {
        this.name = name;
        this.children = children;
    }
}
