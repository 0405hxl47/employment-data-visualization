package com.employment.controller;

import com.employment.service.EmploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FilterController {

    @Autowired
    private EmploymentService employmentService;

    @GetMapping("/filters")
    public Map<String, Object> getFilters() {
        return employmentService.getFilters();
    }
}
