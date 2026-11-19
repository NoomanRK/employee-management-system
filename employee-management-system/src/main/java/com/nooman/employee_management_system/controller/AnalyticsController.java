
package com.nooman.employee_management_system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nooman.employee_management_system.service.AnalyticsService;
import com.nooman.employee_management_system.dto.SalaryOverviewDTO;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Get total employees
    @GetMapping("/total-employees")
    public ResponseEntity<Long> getTotalEmployees() {
        return ResponseEntity.ok(
            analyticsService.getTotalEmployees()
        );
    }

    // Get average salary
    @GetMapping("/average-salary")
    public ResponseEntity<Double> getAverageSalary() {
        return ResponseEntity.ok(
            analyticsService.getAverageSalary()
        );
    }

    // Get department distribution
    @GetMapping("/department-distribution")
    public ResponseEntity<List<Map<String, Object>>> getDepartmentDistribution() {
        return ResponseEntity.ok(
            analyticsService.getDepartmentDistribution()
        );
    }
    
    @GetMapping("/salary-overview")
    public ResponseEntity<List<SalaryOverviewDTO>> getSalaryOverview() {
    	return ResponseEntity.ok(
    			analyticsService.getSalaryOverview()
    			);
    }
}
