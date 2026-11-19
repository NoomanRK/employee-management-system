package com.nooman.employee_management_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.nooman.employee_management_system.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nooman.employee_management_system.dto.EmployeeDTO;

import com.nooman.employee_management_system.response.ApiResponse;
import com.nooman.employee_management_system.response.PaginationResponse;
@RestController // this tells spring, this class will handle Http/Rest requests
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<EmployeeDTO>>> getAllEmployees(
    		@RequestParam(required = false) String search,
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "5") int size,
    		@RequestParam(defaultValue = "id") String sortBy,
    		@RequestParam(defaultValue = "asc") String sortDir) {
    	
        Page<EmployeeDTO> employeePage = employeeService.getAllEmployees(
        		search, 
        		page, 
        		size,
        		sortBy,
        		sortDir
        	);
        
        PaginationResponse<EmployeeDTO> paginationResponse = 
        		new PaginationResponse<>(
        				employeePage.getContent(),
        				employeePage.getNumber(),
        				employeePage.getSize(),
        				employeePage.getTotalElements(),
        				employeePage.getTotalPages()
        				);
        
        ApiResponse<PaginationResponse<EmployeeDTO>> response = 
        		new ApiResponse<>(
        				true,
        				"Employees retrieved successfully",
        				paginationResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(
    		@Valid @RequestBody EmployeeDTO employeeDTO) {   // validates the incoming employee data  	
    	EmployeeDTO savedEmployee = employeeService.createEmployee(employeeDTO);
    	ApiResponse<EmployeeDTO> response = new ApiResponse<>( true, "Employee created successfully", savedEmployee);
    	return ResponseEntity
    			.status(HttpStatus.CREATED) // returns 201 created when an employee is successfully created
    			.body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
    	EmployeeDTO employee = employeeService.getEmployeeById(id);
    	
    	ApiResponse<EmployeeDTO> response = new ApiResponse<>(
    			true, "Employee retrieved successfully", employee);
    	return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
    		@PathVariable Long id,
    		@Valid @RequestBody EmployeeDTO employeeDTO) {
    	
    	EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
    	
    	ApiResponse<EmployeeDTO> response = new ApiResponse<>(
    			true,
    			"Employee updated successfully",
    			updatedEmployee);
    	
    	return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
    		@PathVariable Long id) {
    	
    	employeeService.deleteEmployee(id);
    	
    	ApiResponse<Void> response = 
    			new ApiResponse<>(
    					true,
    					"Employee deleted successfully",
    					null
    					);
    	
    	return ResponseEntity.ok(response);
    }
}