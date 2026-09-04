package com.employment.controller;

import com.employment.query.FilterQuery;
import com.employment.service.EmploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employment")
public class EmploymentController {

    @Autowired
    private EmploymentService employmentService;

    @GetMapping
    public List<Map<String, Object>> getEmploymentList(FilterQuery query) {
        return employmentService.getEmploymentList(query);
    }

    @GetMapping("/export")
    public void exportExcel(FilterQuery query, HttpServletResponse response) {
        employmentService.exportExcel(query, response);
    }
}
