package com.employment.runner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.employment.entity.Employment;
import com.employment.entity.University;
import com.employment.mapper.EmploymentMapper;
import com.employment.mapper.UniversityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private EmploymentMapper employmentMapper;

    @Autowired
    private UniversityBatchService universityBatchService;

    @Autowired
    private EmploymentBatchService employmentBatchService;

    @Value("${app.data-dir}")
    private String dataDir;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        Long count = employmentMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("Data already exists ({} records), skipping import.", count);
            return;
        }

        log.info("No data found, starting CSV import...");

        // Resolve data directory relative to the application working directory
        File dataDirFile = new File(dataDir);
        if (!dataDirFile.isAbsolute()) {
            // Try relative to jar/working dir
            dataDirFile = new File(System.getProperty("user.dir"), dataDir);
        }
        log.info("Data directory: {}", dataDirFile.getAbsolutePath());

        // 1. Import universities.csv
        File uniFile = new File(dataDirFile, "universities.csv");
        if (uniFile.exists()) {
            importUniversities(uniFile);
        } else {
            log.warn("universities.csv not found at {}", uniFile.getAbsolutePath());
        }

        // 2. Import employment_data.csv
        File empFile = new File(dataDirFile, "employment_data.csv");
        if (empFile.exists()) {
            importEmployment(empFile);
        } else {
            log.warn("employment_data.csv not found at {}", empFile.getAbsolutePath());
        }

        log.info("CSV import completed!");
    }

    private void importUniversities(File file) throws Exception {
        log.info("Importing universities from: {}", file.getAbsolutePath());
        List<University> batch = new ArrayList<>();
        int total = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // skip header
            // strip BOM if present
            if (header != null && header.length() > 0 && header.charAt(0) == '﻿') {
                header = header.substring(1);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = parseCsvLine(line);
                if (parts.length < 4) continue;

                University u = new University();
                u.setUniversityName(clean(parts[0]));
                u.setUniversityType(clean(parts[1]));
                u.setProvince(clean(parts[2]));
                u.setCity(clean(parts[3]));

                batch.add(u);
                total++;

                if (batch.size() >= BATCH_SIZE) {
                    universityBatchService.saveBatch(batch, BATCH_SIZE);
                    batch.clear();
                    log.info("Universities imported: {}", total);
                }
            }
        }

        if (!batch.isEmpty()) {
            universityBatchService.saveBatch(batch, BATCH_SIZE);
        }
        log.info("Universities import complete. Total: {}", total);
    }

    private void importEmployment(File file) throws Exception {
        log.info("Importing employment data from: {}", file.getAbsolutePath());
        List<Employment> batch = new ArrayList<>();
        int total = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // skip header
            // Header: id,university_name,graduation_year,major_name,degree,employment_rate,
            //         employment_type,industry,province,city,salary_min,salary_max,salary_avg,
            //         salary_true,achievement_rate,data_source
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = parseCsvLine(line);
                if (parts.length < 16) continue;

                Employment e = new Employment();
                // Skip parts[0] (id) - auto increment
                e.setUniversityName(clean(parts[1]));
                e.setGraduationYear(parseInteger(parts[2]));
                e.setMajorName(clean(parts[3]));
                e.setDegree(clean(parts[4]));
                e.setEmploymentRate(parseBigDecimal(parts[5]));
                e.setEmploymentType(clean(parts[6]));
                e.setIndustry(clean(parts[7]));
                e.setProvince(clean(parts[8]));
                e.setCity(clean(parts[9]));
                e.setSalaryMin(parseBigDecimal(parts[10]));
                e.setSalaryMax(parseBigDecimal(parts[11]));
                e.setSalaryAvg(parseBigDecimal(parts[12]));
                e.setSalaryTrue(parseBigDecimal(parts[13]));
                e.setAchievementRate(parseBigDecimal(parts[14]));
                e.setDataSource(clean(parts[15]));

                batch.add(e);
                total++;

                if (batch.size() >= BATCH_SIZE) {
                    employmentBatchService.saveBatch(batch, BATCH_SIZE);
                    batch.clear();
                    if (total % 5000 == 0) {
                        log.info("Employment records imported: {}", total);
                    }
                }
            }
        }

        if (!batch.isEmpty()) {
            employmentBatchService.saveBatch(batch, BATCH_SIZE);
        }
        log.info("Employment import complete. Total: {}", total);
    }

    /**
     * Simple CSV parser that handles quoted fields with commas inside.
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());

        return fields.toArray(new String[0]);
    }

    private String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private Integer parseInteger(String s) {
        s = clean(s);
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String s) {
        s = clean(s);
        if (s == null) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Inner batch service classes for MyBatis-Plus saveBatch support

    @Component
    public static class UniversityBatchService extends ServiceImpl<UniversityMapper, University> {
    }

    @Component
    public static class EmploymentBatchService extends ServiceImpl<EmploymentMapper, Employment> {
    }
}
