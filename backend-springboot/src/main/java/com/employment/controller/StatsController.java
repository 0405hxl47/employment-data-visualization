package com.employment.controller;

import com.employment.dto.*;
import com.employment.query.FilterQuery;
import com.employment.service.EmploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private EmploymentService employmentService;

    @GetMapping("/overview")
    public OverviewDTO getOverview() {
        return employmentService.getOverview();
    }

    @GetMapping("/salary_by_university")
    public List<Map<String, Object>> getSalaryByUniversity(FilterQuery query) {
        return employmentService.getSalaryByUniversity(query);
    }

    @GetMapping("/employment_type")
    public List<Map<String, Object>> getEmploymentType(FilterQuery query) {
        return employmentService.getEmploymentTypeStats(query);
    }

    @GetMapping("/industry")
    public List<Map<String, Object>> getIndustry(FilterQuery query) {
        return employmentService.getIndustryStats(query);
    }

    @GetMapping("/province")
    public List<Map<String, Object>> getProvince(FilterQuery query) {
        return employmentService.getProvinceStats(query);
    }

    @GetMapping("/salary_by_degree")
    public List<Map<String, Object>> getSalaryByDegree(FilterQuery query) {
        return employmentService.getSalaryByDegree(query);
    }

    @GetMapping("/sankey")
    public SankeyDTO getSankey(FilterQuery query) {
        return employmentService.getSankeyData(query);
    }

    @GetMapping("/treemap")
    public List<Map<String, Object>> getTreemap(FilterQuery query) {
        return employmentService.getTreemapData(query);
    }

    @GetMapping("/sunburst")
    public List<SunburstNodeDTO> getSunburst(FilterQuery query) {
        return employmentService.getSunburstData(query);
    }

    @GetMapping("/city")
    public List<Map<String, Object>> getCity(FilterQuery query) {
        return employmentService.getCityStats(query);
    }

    @GetMapping("/salary_box")
    public List<BoxPlotDTO> getSalaryBox(FilterQuery query,
                                          @RequestParam(value = "group_by", required = false, defaultValue = "industry") String groupBy) {
        return employmentService.getSalaryBoxData(query, groupBy);
    }

    @GetMapping("/scatter")
    public List<Map<String, Object>> getScatter(FilterQuery query) {
        return employmentService.getScatterData(query);
    }

    @GetMapping("/further_education")
    public List<Map<String, Object>> getFurtherEducation(FilterQuery query) {
        return employmentService.getFurtherEducationData(query);
    }

    @GetMapping("/province_by_university")
    public List<Map<String, Object>> getProvinceByUniversity(FilterQuery query) {
        return employmentService.getProvinceByUniversity(query);
    }

    @GetMapping("/wordcloud")
    public List<Map<String, Object>> getWordcloud(FilterQuery query) {
        return employmentService.getWordcloudData(query);
    }

    @GetMapping("/trend")
    public List<TrendDTO> getTrend(FilterQuery query) {
        return employmentService.getTrendData(query);
    }

    @GetMapping("/major_analysis")
    public Map<String, Object> getMajorAnalysis(FilterQuery query,
                                                 @RequestParam(value = "majorName", required = false) String majorName) {
        return employmentService.getMajorAnalysis(query, majorName);
    }
}
