
package com.nooman.employee_management_system.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nooman.employee_management_system.repository.EmployeeRepository;
import com.nooman.employee_management_system.service.AnalyticsService;
import com.nooman.employee_management_system.dto.SalaryOverviewDTO;;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EmployeeRepository employeeRepository;

    public AnalyticsServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public long getTotalEmployees() {
        return employeeRepository.count();
    }

    @Override
    public Double getAverageSalary() {
        Double averageSalary = employeeRepository.findAverageSalary();

        return averageSalary != null ? averageSalary : 0.0;
    }

    @Override
    public List<Map<String, Object>> getDepartmentDistribution() {

        return employeeRepository.countEmployeesByDepartment()
                .stream()
                .map(result -> Map.<String, Object>of(
                        "department", result[0],
                        "employeeCount", result[1]
                ))
                .toList();
    }
    
    @Override
    public List<SalaryOverviewDTO> getSalaryOverview() {
    	return employeeRepository.findAllEmployeeSalaries();
    }
}
