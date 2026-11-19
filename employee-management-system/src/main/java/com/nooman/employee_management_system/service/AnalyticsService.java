package com.nooman.employee_management_system.service;

import java.util.List;
import java.util.Map;
import com.nooman.employee_management_system.dto.SalaryOverviewDTO;

public interface AnalyticsService {

    long getTotalEmployees();

    Double getAverageSalary();

    List<Map<String, Object>> getDepartmentDistribution();

    List<SalaryOverviewDTO> getSalaryOverview();
}
