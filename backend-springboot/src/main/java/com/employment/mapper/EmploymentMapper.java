package com.employment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.employment.entity.Employment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmploymentMapper extends BaseMapper<Employment> {

    @Select("SELECT COUNT(*) FROM employment")
    Long countAll();

    @Select("SELECT COUNT(DISTINCT university_name) FROM employment")
    Long countDistinctUniversities();

    @Select("SELECT COUNT(DISTINCT major_name) FROM employment")
    Long countDistinctMajors();

    @Select("SELECT ROUND(AVG(salary_true), 0) FROM employment WHERE salary_true IS NOT NULL")
    BigDecimal avgSalary();

    @Select("SELECT ROUND(AVG(avg_rate), 1) FROM (" +
            "  SELECT university_name, major_name, degree, AVG(employment_rate) as avg_rate " +
            "  FROM employment " +
            "  WHERE employment_rate IS NOT NULL AND employment_rate <= 100 " +
            "  GROUP BY university_name, major_name, degree" +
            ") sub")
    BigDecimal avgEmploymentRate();

    @Select("SELECT DISTINCT degree FROM employment WHERE degree IS NOT NULL AND degree != '' ORDER BY degree")
    List<String> selectDistinctDegrees();

    @Select("SELECT DISTINCT employment_type FROM employment WHERE employment_type IS NOT NULL AND employment_type != '' ORDER BY employment_type")
    List<String> selectDistinctEmploymentTypes();

    @Select("SELECT DISTINCT industry FROM employment WHERE industry IS NOT NULL AND industry != '' ORDER BY industry")
    List<String> selectDistinctIndustries();

    @Select("SELECT DISTINCT province FROM employment WHERE province IS NOT NULL AND province != '' ORDER BY province")
    List<String> selectDistinctProvinces();

    @Select("SELECT DISTINCT graduation_year FROM employment WHERE graduation_year IS NOT NULL ORDER BY graduation_year")
    List<Integer> selectDistinctGraduationYears();
}
