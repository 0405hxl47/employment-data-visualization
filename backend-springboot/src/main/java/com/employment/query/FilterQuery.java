package com.employment.query;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class FilterQuery {

    private String university_name;

    private String degree;

    private String employment_type;

    private String industry;

    private String province;

    private String university_type;

    private Integer graduation_year;

    private String keyword;

    public String getUniversityName() { return university_name; }
    public String getEmploymentType() { return employment_type; }
    public String getUniversityType() { return university_type; }
    public Integer getGraduationYear() { return graduation_year; }
}
