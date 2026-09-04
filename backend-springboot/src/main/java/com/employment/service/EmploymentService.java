package com.employment.service;

import com.employment.dto.*;
import com.employment.query.FilterQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface EmploymentService {

    Map<String, Object> getFilters();

    List<Map<String, Object>> getEmploymentList(FilterQuery query);

    OverviewDTO getOverview();

    List<Map<String, Object>> getSalaryByUniversity(FilterQuery query);

    List<Map<String, Object>> getEmploymentTypeStats(FilterQuery query);

    List<Map<String, Object>> getIndustryStats(FilterQuery query);

    List<Map<String, Object>> getProvinceStats(FilterQuery query);

    List<Map<String, Object>> getSalaryByDegree(FilterQuery query);

    SankeyDTO getSankeyData(FilterQuery query);

    List<Map<String, Object>> getTreemapData(FilterQuery query);

    List<SunburstNodeDTO> getSunburstData(FilterQuery query);

    List<Map<String, Object>> getCityStats(FilterQuery query);

    List<BoxPlotDTO> getSalaryBoxData(FilterQuery query, String groupBy);

    List<Map<String, Object>> getScatterData(FilterQuery query);

    List<Map<String, Object>> getFurtherEducationData(FilterQuery query);

    List<Map<String, Object>> getProvinceByUniversity(FilterQuery query);

    List<Map<String, Object>> getWordcloudData(FilterQuery query);

    List<TrendDTO> getTrendData(FilterQuery query);

    Map<String, Object> getMajorAnalysis(FilterQuery query, String majorName);

    void exportExcel(FilterQuery query, HttpServletResponse response);
}
