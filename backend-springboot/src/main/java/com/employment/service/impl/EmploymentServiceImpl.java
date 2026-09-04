package com.employment.service.impl;

import com.alibaba.excel.EasyExcel;
import com.employment.dto.*;
import com.employment.entity.Employment;
import com.employment.entity.University;
import com.employment.mapper.EmploymentMapper;
import com.employment.mapper.UniversityMapper;
import com.employment.query.FilterQuery;
import com.employment.service.EmploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmploymentServiceImpl implements EmploymentService {

    @Autowired
    private EmploymentMapper employmentMapper;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.data-dir:../data}")
    private String dataDir;

    /** Pre-computed low-rate majors loaded from CSV at startup */
    private List<Map<String, Object>> lowRateCache = Collections.emptyList();

    @PostConstruct
    public void loadLowRateCache() {
        File csvFile = new File(dataDir, "low_rate_majors.csv");
        if (!csvFile.exists()) {
            System.out.println("[WARN] low_rate_majors.csv not found at " + csvFile.getAbsolutePath());
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
            String header = br.readLine();
            if (header != null && header.startsWith("﻿")) {
                header = header.substring(1);
            }
            // header: university_name,major_name,avg_rate,count
            List<Map<String, Object>> list = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("university_name", parts[0].trim());
                row.put("major_name", parts[1].trim());
                row.put("avg_rate", new BigDecimal(parts[2].trim()));
                row.put("count", Integer.parseInt(parts[3].trim()));
                list.add(row);
            }
            lowRateCache = list;
            System.out.println("[INFO] Loaded " + list.size() + " low-rate majors from CSV cache");
        } catch (Exception e) {
            System.out.println("[WARN] Failed to load low_rate_majors.csv: " + e.getMessage());
        }
    }

    // ===================== Helper: build WHERE clause from filters =====================

    private String buildWhereClause(FilterQuery query, List<Object> params, boolean hasUniversityJoin) {
        StringBuilder sb = new StringBuilder(" WHERE 1=1");

        if (query == null) return sb.toString();

        if (isNotEmpty(query.getUniversityName())) {
            sb.append(" AND e.university_name = ?");
            params.add(query.getUniversityName());
        }
        if (isNotEmpty(query.getDegree())) {
            sb.append(" AND e.degree = ?");
            params.add(query.getDegree());
        }
        if (isNotEmpty(query.getEmploymentType())) {
            sb.append(" AND e.employment_type = ?");
            params.add(query.getEmploymentType());
        }
        if (isNotEmpty(query.getIndustry())) {
            sb.append(" AND e.industry = ?");
            params.add(query.getIndustry());
        }
        if (isNotEmpty(query.getProvince())) {
            sb.append(" AND e.province = ?");
            params.add(query.getProvince());
        }
        if (query.getGraduationYear() != null) {
            sb.append(" AND e.graduation_year = ?");
            params.add(query.getGraduationYear());
        }
        if (isNotEmpty(query.getKeyword())) {
            sb.append(" AND (e.major_name LIKE ? OR e.university_name LIKE ?)");
            String kw = "%" + query.getKeyword() + "%";
            params.add(kw);
            params.add(kw);
        }
        if (isNotEmpty(query.getUniversityType())) {
            if (!hasUniversityJoin) {
                // This case shouldn't happen if caller already added the join,
                // but we guard it anyway
            }
            if ("双一流".equals(query.getUniversityType())) {
                sb.append(" AND u.university_type IN ('985', '211')");
            } else {
                sb.append(" AND u.university_type = ?");
                params.add(query.getUniversityType());
            }
        }

        return sb.toString();
    }

    private boolean needsUniversityJoin(FilterQuery query) {
        return query != null && isNotEmpty(query.getUniversityType());
    }

    private String buildFromClause(FilterQuery query) {
        if (needsUniversityJoin(query)) {
            return " FROM employment e LEFT JOIN universities u ON e.university_name = u.university_name";
        }
        return " FROM employment e";
    }

    private boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // ===================== 1. GET /api/filters =====================

    @Override
    public Map<String, Object> getFilters() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("degree", employmentMapper.selectDistinctDegrees());
        result.put("employment_type", employmentMapper.selectDistinctEmploymentTypes());
        result.put("industry", employmentMapper.selectDistinctIndustries());
        result.put("province", employmentMapper.selectDistinctProvinces());

        // university_types: distinct from universities table
        List<String> uTypes = jdbcTemplate.queryForList(
                "SELECT DISTINCT university_type FROM universities WHERE university_type IS NOT NULL AND university_type != '' ORDER BY university_type",
                String.class);
        result.put("university_types", uTypes);

        // universities_detail
        List<University> universities = universityMapper.selectList(null);
        List<Map<String, Object>> uDetail = universities.stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("university_name", u.getUniversityName());
            m.put("university_type", u.getUniversityType());
            m.put("province", u.getProvince());
            m.put("city", u.getCity());
            return m;
        }).collect(Collectors.toList());
        result.put("universities_detail", uDetail);

        result.put("graduation_years", employmentMapper.selectDistinctGraduationYears());

        return result;
    }

    // ===================== 2. GET /api/employment =====================

    @Override
    public List<Map<String, Object>> getEmploymentList(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.*, u.university_type"
                + " FROM employment e LEFT JOIN universities u ON e.university_name = u.university_name"
                + buildWhereClause(query, params, true)
                + " LIMIT 10";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 3. GET /api/stats/overview =====================

    @Override
    public OverviewDTO getOverview() {
        OverviewDTO dto = new OverviewDTO();
        dto.setTotalRecords(employmentMapper.countAll());
        dto.setUniversityCount(employmentMapper.countDistinctUniversities());
        dto.setMajorCount(employmentMapper.countDistinctMajors());
        dto.setAvgSalary(employmentMapper.avgSalary());
        dto.setAvgEmploymentRate(employmentMapper.avgEmploymentRate());
        return dto;
    }

    // ===================== 4. GET /api/stats/salary_by_university =====================

    @Override
    public List<Map<String, Object>> getSalaryByUniversity(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        // Always join universities for university_type in result
        String sql = "SELECT e.university_name, "
                + "ROUND(AVG(e.salary_true), 0) as avg_salary, "
                + "COUNT(*) as sample_count, "
                + "u.university_type"
                + " FROM employment e LEFT JOIN universities u ON e.university_name = u.university_name"
                + buildWhereClause(query, params, true)
                + " AND e.salary_true IS NOT NULL"
                + " GROUP BY e.university_name, u.university_type"
                + " ORDER BY avg_salary DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 5. GET /api/stats/employment_type =====================

    @Override
    public List<Map<String, Object>> getEmploymentTypeStats(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.employment_type, COUNT(*) as count"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.employment_type IS NOT NULL AND e.employment_type != ''"
                + " GROUP BY e.employment_type"
                + " ORDER BY count DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 6. GET /api/stats/industry =====================

    @Override
    public List<Map<String, Object>> getIndustryStats(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.industry, COUNT(*) as count, ROUND(AVG(e.salary_true), 0) as avg_salary"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.industry IS NOT NULL AND e.industry != ''"
                + " GROUP BY e.industry"
                + " ORDER BY count DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 7. GET /api/stats/province =====================

    @Override
    public List<Map<String, Object>> getProvinceStats(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.province, COUNT(*) as count, ROUND(AVG(e.salary_true), 0) as avg_salary"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.province IS NOT NULL AND e.province != ''"
                + " GROUP BY e.province"
                + " ORDER BY count DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 8. GET /api/stats/salary_by_degree =====================

    @Override
    public List<Map<String, Object>> getSalaryByDegree(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.degree, "
                + "ROUND(AVG(e.salary_true), 0) as avg_salary, "
                + "ROUND(MIN(e.salary_true), 0) as min_salary, "
                + "ROUND(MAX(e.salary_true), 0) as max_salary, "
                + "COUNT(*) as sample_count"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.salary_true IS NOT NULL"
                + " GROUP BY e.degree"
                + " ORDER BY avg_salary DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 9. GET /api/stats/sankey =====================

    @Override
    public SankeyDTO getSankeyData(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.degree, e.employment_type, e.industry, COUNT(*) as cnt"
                + " FROM employment e LEFT JOIN universities u ON e.university_name = u.university_name"
                + buildWhereClause(query, params, true)
                + " AND e.employment_type IS NOT NULL AND e.employment_type != ''"
                + " AND e.industry IS NOT NULL AND e.industry != ''"
                + " GROUP BY e.degree, e.employment_type, e.industry"
                + " HAVING cnt >= 5";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());

        Set<String> nodeSet = new LinkedHashSet<>();
        // degree -> type links
        Map<String, Long> degreeTypeLinks = new LinkedHashMap<>();
        // type -> industry links
        Map<String, Long> typeIndustryLinks = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            String degree = (String) row.get("degree");
            String type = (String) row.get("employment_type");
            String industry = (String) row.get("industry");
            long cnt = ((Number) row.get("cnt")).longValue();

            if (degree != null) nodeSet.add(degree);
            if (type != null) nodeSet.add(type);
            if (industry != null) nodeSet.add(industry);

            if (degree != null && type != null) {
                String key1 = degree + "||" + type;
                degreeTypeLinks.merge(key1, cnt, Long::sum);
            }
            if (type != null && industry != null) {
                String key2 = type + "||" + industry;
                typeIndustryLinks.merge(key2, cnt, Long::sum);
            }
        }

        SankeyDTO dto = new SankeyDTO();
        dto.setNodes(nodeSet.stream().map(SankeyDTO.SankeyNode::new).collect(Collectors.toList()));

        List<SankeyDTO.SankeyLink> links = new ArrayList<>();
        degreeTypeLinks.forEach((k, v) -> {
            String[] parts = k.split("\\|\\|");
            links.add(new SankeyDTO.SankeyLink(parts[0], parts[1], v));
        });
        typeIndustryLinks.forEach((k, v) -> {
            String[] parts = k.split("\\|\\|");
            links.add(new SankeyDTO.SankeyLink(parts[0], parts[1], v));
        });
        dto.setLinks(links);

        return dto;
    }

    // ===================== 10. GET /api/stats/treemap =====================

    @Override
    public List<Map<String, Object>> getTreemapData(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.industry as name, COUNT(*) as value"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.industry IS NOT NULL AND e.industry != ''"
                + " GROUP BY e.industry"
                + " ORDER BY value DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 11. GET /api/stats/sunburst =====================

    @Override
    public List<SunburstNodeDTO> getSunburstData(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.degree, e.employment_type, e.industry, COUNT(*) as cnt"
                + " FROM employment e LEFT JOIN universities u ON e.university_name = u.university_name"
                + buildWhereClause(query, params, true)
                + " AND e.employment_type IS NOT NULL AND e.employment_type != ''"
                + " AND e.industry IS NOT NULL AND e.industry != ''"
                + " GROUP BY e.degree, e.employment_type, e.industry";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());

        // Build 3-level tree: degree -> type -> industry
        // degree -> (type -> (industry -> count))
        Map<String, Map<String, Map<String, Long>>> tree = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String degree = (String) row.get("degree");
            String type = (String) row.get("employment_type");
            String industry = (String) row.get("industry");
            long cnt = ((Number) row.get("cnt")).longValue();

            if (degree == null || type == null || industry == null) continue;

            tree.computeIfAbsent(degree, k -> new LinkedHashMap<>())
                    .computeIfAbsent(type, k -> new LinkedHashMap<>())
                    .merge(industry, cnt, Long::sum);
        }

        List<SunburstNodeDTO> result = new ArrayList<>();
        tree.forEach((degree, typeMap) -> {
            SunburstNodeDTO degreeNode = new SunburstNodeDTO();
            degreeNode.setName(degree);
            List<SunburstNodeDTO> typeChildren = new ArrayList<>();

            typeMap.forEach((type, industryMap) -> {
                SunburstNodeDTO typeNode = new SunburstNodeDTO();
                typeNode.setName(type);
                List<SunburstNodeDTO> industryChildren = new ArrayList<>();

                industryMap.forEach((industry, cnt) -> {
                    SunburstNodeDTO industryNode = new SunburstNodeDTO();
                    industryNode.setName(industry);
                    industryNode.setValue(cnt);
                    industryChildren.add(industryNode);
                });

                typeNode.setChildren(industryChildren);
                typeChildren.add(typeNode);
            });

            degreeNode.setChildren(typeChildren);
            result.add(degreeNode);
        });

        return result;
    }

    // ===================== 12. GET /api/stats/city =====================

    @Override
    public List<Map<String, Object>> getCityStats(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.city, e.province, COUNT(*) as count, ROUND(AVG(e.salary_true), 0) as avg_salary"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.city IS NOT NULL AND e.city != ''"
                + " GROUP BY e.city, e.province"
                + " ORDER BY count DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 13. GET /api/stats/salary_box =====================

    @Override
    public List<BoxPlotDTO> getSalaryBoxData(FilterQuery query, String groupBy) {
        if (groupBy == null || groupBy.isEmpty()) {
            groupBy = "industry";
        }

        String groupColumn;
        switch (groupBy) {
            case "degree":
                groupColumn = "e.degree";
                break;
            case "university_name":
                groupColumn = "e.university_name";
                break;
            default:
                groupColumn = "e.industry";
                break;
        }

        List<Object> params = new ArrayList<>();
        String sql = "SELECT " + groupColumn + " as group_name, e.salary_true"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.salary_true IS NOT NULL"
                + " AND " + groupColumn + " IS NOT NULL AND " + groupColumn + " != ''";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());

        // Group values
        Map<String, List<BigDecimal>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String name = (String) row.get("group_name");
            BigDecimal salary = toBigDecimal(row.get("salary_true"));
            if (name != null && salary != null) {
                grouped.computeIfAbsent(name, k -> new ArrayList<>()).add(salary);
            }
        }

        // Compute box plot for groups with >= 4 samples
        List<BoxPlotDTO> result = new ArrayList<>();
        grouped.forEach((name, values) -> {
            if (values.size() >= 4) {
                Collections.sort(values);
                result.add(new BoxPlotDTO(name, values));
            }
        });

        // Sort by median DESC
        result.sort((a, b) -> b.getMedian().compareTo(a.getMedian()));
        return result;
    }

    // ===================== 14. GET /api/stats/scatter =====================

    @Override
    public List<Map<String, Object>> getScatterData(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.major_name, e.degree, e.university_name, "
                + "ROUND(AVG(e.employment_rate), 1) as employment_rate, "
                + "ROUND(AVG(e.salary_true), 0) as salary_avg, "
                + "COUNT(*) as sample_count"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.salary_true IS NOT NULL"
                + " AND e.employment_rate IS NOT NULL AND e.employment_rate <= 100"
                + " GROUP BY e.university_name, e.major_name, e.degree";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 15. GET /api/stats/further_education =====================

    @Override
    public List<Map<String, Object>> getFurtherEducationData(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.university_name, e.employment_type, COUNT(*) as count"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.employment_type IN ('升学', '出国留学', '考公', '高校/科研')"
                + " GROUP BY e.university_name, e.employment_type";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());

        // Pivot: group by university_name, create columns for each type
        Map<String, Map<String, Object>> pivotMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String uniName = (String) row.get("university_name");
            String empType = (String) row.get("employment_type");
            long count = ((Number) row.get("count")).longValue();

            Map<String, Object> uniMap = pivotMap.computeIfAbsent(uniName, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("university_name", k);
                m.put("升学", 0L);
                m.put("出国留学", 0L);
                m.put("考公", 0L);
                m.put("高校/科研", 0L);
                return m;
            });
            uniMap.put(empType, count);
        }

        return new ArrayList<>(pivotMap.values());
    }

    // ===================== 16. GET /api/stats/province_by_university =====================

    @Override
    public List<Map<String, Object>> getProvinceByUniversity(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.university_name, e.province, COUNT(*) as count"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.province IS NOT NULL AND e.province != ''"
                + " GROUP BY e.university_name, e.province"
                + " ORDER BY e.university_name, count DESC";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 17. GET /api/stats/wordcloud =====================

    @Override
    public List<Map<String, Object>> getWordcloudData(FilterQuery query) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT e.major_name as name, COUNT(*) as value"
                + buildFromClause(query)
                + buildWhereClause(query, params, needsUniversityJoin(query))
                + " AND e.major_name IS NOT NULL AND e.major_name != ''"
                + " GROUP BY e.major_name"
                + " ORDER BY value DESC"
                + " LIMIT 100";
        return jdbcTemplate.queryForList(sql, params.toArray());
    }

    // ===================== 18. GET /api/stats/trend =====================

    @Override
    public List<TrendDTO> getTrendData(FilterQuery query) {
        String sql = "SELECT e.graduation_year, "
                + "ROUND(AVG(e.salary_true), 0) as avg_salary, "
                + "ROUND(AVG(CASE WHEN e.employment_rate IS NOT NULL AND e.employment_rate <= 100 THEN e.employment_rate END), 1) as avg_rate, "
                + "COUNT(*) as record_count"
                + " FROM employment e"
                + " WHERE e.university_name = '东北大学'"
                + " AND e.graduation_year BETWEEN 2019 AND 2023"
                + " GROUP BY e.graduation_year"
                + " ORDER BY e.graduation_year";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        return rows.stream().map(row -> {
            TrendDTO dto = new TrendDTO();
            dto.setGraduationYear(((Number) row.get("graduation_year")).intValue());
            dto.setAvgSalary(toBigDecimal(row.get("avg_salary")));
            dto.setAvgRate(toBigDecimal(row.get("avg_rate")));
            dto.setRecordCount(((Number) row.get("record_count")).longValue());
            return dto;
        }).collect(Collectors.toList());
    }

    // ===================== 19. GET /api/stats/major_analysis =====================

    @Override
    public Map<String, Object> getMajorAnalysis(FilterQuery query, String majorName) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. industry_distribution
        {
            List<Object> params = new ArrayList<>();
            String sql = "SELECT e.industry as name, COUNT(*) as value"
                    + buildFromClause(query)
                    + buildWhereClause(query, params, needsUniversityJoin(query))
                    + " AND e.industry IS NOT NULL AND e.industry != ''";
            if (isNotEmpty(majorName)) {
                sql += " AND e.major_name = ?";
                params.add(majorName);
            }
            sql += " GROUP BY e.industry ORDER BY value DESC";
            result.put("industry_distribution", jdbcTemplate.queryForList(sql, params.toArray()));
        }

        // 2. salary_stats
        {
            List<Object> params = new ArrayList<>();
            String sql = "SELECT e.major_name, "
                    + "ROUND(AVG(e.salary_true), 0) as avg_salary, "
                    + "ROUND(MIN(e.salary_true), 0) as min_salary, "
                    + "ROUND(MAX(e.salary_true), 0) as max_salary, "
                    + "COUNT(*) as count"
                    + buildFromClause(query)
                    + buildWhereClause(query, params, needsUniversityJoin(query))
                    + " AND e.salary_true IS NOT NULL";
            if (isNotEmpty(majorName)) {
                sql += " AND e.major_name = ?";
                params.add(majorName);
            }
            sql += " GROUP BY e.major_name ORDER BY avg_salary DESC";
            result.put("salary_stats", jdbcTemplate.queryForList(sql, params.toArray()));
        }

        // 3. top_cities
        {
            List<Object> params = new ArrayList<>();
            String sql = "SELECT e.city, e.province, COUNT(*) as count, ROUND(AVG(e.salary_true), 0) as avg_salary"
                    + buildFromClause(query)
                    + buildWhereClause(query, params, needsUniversityJoin(query))
                    + " AND e.city IS NOT NULL AND e.city != ''";
            if (isNotEmpty(majorName)) {
                sql += " AND e.major_name = ?";
                params.add(majorName);
            }
            sql += " GROUP BY e.city, e.province ORDER BY count DESC";
            result.put("top_cities", jdbcTemplate.queryForList(sql, params.toArray()));
        }

        // 4. low_rate data from pre-computed CSV cache (instant, no DB query)
        {
            result.put("low_rate_count", lowRateCache.size());
            result.put("low_rate_majors", lowRateCache.size() > 5 ? lowRateCache.subList(0, 5) : lowRateCache);
            result.put("low_rate_all", lowRateCache);
        }

        return result;
    }

    // ===================== 20. GET /api/employment/export =====================

    @Override
    public void exportExcel(FilterQuery query, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("employment_data", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            // Query all data with filters (no LIMIT)
            List<Object> params = new ArrayList<>();
            String sql = "SELECT e.*, u.university_type"
                    + " FROM employment e LEFT JOIN universities u ON e.university_name = u.university_name"
                    + buildWhereClause(query, params, true);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());

            // Convert to list of lists for EasyExcel
            List<List<String>> headers = new ArrayList<>();
            String[] headerNames = {"ID", "学校名称", "毕业年份", "专业名称", "学历", "就业率",
                    "就业类型", "行业", "省份", "城市", "最低薪资", "最高薪资",
                    "平均薪资", "真实薪资", "达成率", "数据来源", "学校类型"};
            for (String h : headerNames) {
                List<String> head = new ArrayList<>();
                head.add(h);
                headers.add(head);
            }

            List<List<Object>> dataRows = new ArrayList<>();
            String[] fields = {"id", "university_name", "graduation_year", "major_name", "degree",
                    "employment_rate", "employment_type", "industry", "province", "city",
                    "salary_min", "salary_max", "salary_avg", "salary_true",
                    "achievement_rate", "data_source", "university_type"};

            for (Map<String, Object> row : rows) {
                List<Object> dataRow = new ArrayList<>();
                for (String field : fields) {
                    Object val = row.get(field);
                    dataRow.add(val != null ? val : "");
                }
                dataRows.add(dataRow);
            }

            EasyExcel.write(response.getOutputStream())
                    .head(headers)
                    .sheet("就业数据")
                    .doWrite(dataRows);

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    // ===================== Utility =====================

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return null;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return new BigDecimal(obj.toString());
        try {
            return new BigDecimal(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
