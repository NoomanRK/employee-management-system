package com.nooman.employee_management_system.service;

import org.springframework.data.domain.Page;

import com.nooman.employee_management_system.dto.EmployeeDTO;


public interface EmployeeService {

	EmployeeDTO createEmployee(EmployeeDTO employee);
	 Page<EmployeeDTO> getAllEmployees(
			 String search, 
			 int page, 
			 int size,
			 String sortBy,
			 String sortDir);

	    EmployeeDTO getEmployeeById(Long id);

	    EmployeeDTO updateEmployee(Long id, EmployeeDTO employee);

	    void deleteEmployee(Long id);
}
