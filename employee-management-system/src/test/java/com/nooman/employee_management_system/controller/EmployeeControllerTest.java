package com.nooman.employee_management_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nooman.employee_management_system.dto.EmployeeDTO;
import com.nooman.employee_management_system.entity.Employee;
import com.nooman.employee_management_system.exception.EmployeeNotFoundException;
import com.nooman.employee_management_system.security.JwtService;
import com.nooman.employee_management_system.service.EmployeeService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

	@MockitoBean
	private EmployeeService employeeService;
	
	@MockitoBean
	private JwtService jwtService;
	
	@MockitoBean
	private UserDetailsService userDetailsService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void getAllEmployees_shouldReturnEmployee() throws Exception {
		
		EmployeeDTO employee1 = new EmployeeDTO();
		 employee1.setId(1L);
		    employee1.setFirstName("John");
		    employee1.setLastName("Doe");
		    employee1.setEmail("john@gmail.com");

		    EmployeeDTO employee2 = new EmployeeDTO();
		    employee2.setId(2L);
		    employee2.setFirstName("Jane");
		    employee2.setLastName("Smith");
		    employee2.setEmail("jane@gmail.com");
		    
		    Page<EmployeeDTO> employeePage = new PageImpl<>(List.of(employee1, employee2));
		    
		    when(employeeService.getAllEmployees(
		    		null,
		    		0,
		    		5,
		    		"id",
		    		"asc"
		    		)).thenReturn(employeePage);
		    
		    mockMvc.perform(get("/api/employees"))
		    .andExpect(status().isOk())
		    .andExpect(jsonPath("$.success").value(true))
		    .andExpect(jsonPath("$.message")
		    		.value("Employees retrieved successfully"))
		    .andExpect(jsonPath("$.data.content[0].id").value(1))
		    .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
		    .andExpect(jsonPath("$.data.content[1].id").value(2))
		    .andExpect(jsonPath("$.data.content[1].firstName").value("Jane"));
	}
	
	@Test
	void getEmployeeById_shouldReturnEmployee_whenIdExists() throws Exception {
		
		 EmployeeDTO employee = new EmployeeDTO();
		    employee.setId(1L);
		    employee.setFirstName("John");
		    employee.setLastName("Doe");
		    employee.setEmail("john@gmail.com");
		    
		    when(employeeService.getEmployeeById(1L)).thenReturn(employee);
		    
		    mockMvc.perform(get("/api/employees/1"))
		    .andExpect(status().isOk())
		    .andExpect(jsonPath("$.success").value(true))
		    .andExpect(jsonPath("$.message").value("Employee retrieved successfully"))
		    .andExpect(jsonPath("$.data.id").value(1))
		    .andExpect(jsonPath("$.data.firstName").value("John"))
		    .andExpect(jsonPath("$.data.lastName").value("Doe"))
		    .andExpect(jsonPath("$.data.email").value("john@gmail.com"));
	}
	
	@Test 
	void getEmployeeById_shouldReturn404_whenEmployeeDoesNotExist() throws Exception {
		
		when(employeeService.getEmployeeById(999L)).thenThrow(
				new EmployeeNotFoundException(
						"Employee not found with id: 999"
						)
				
				);
		
		mockMvc.perform(get("/api/employees/999"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.success").value(false))
		.andExpect(jsonPath("$.message").value("Employee not found with id: 999"))
		.andExpect(jsonPath("$.data").isEmpty());
	}
	
	@Test
	void createEmployee_shouldReturn201_whenEmployeeIsCreated() throws Exception {
		
		EmployeeDTO employeeDTO = new EmployeeDTO();
		 employeeDTO.setId(1L);
		    employeeDTO.setFirstName("John");
		    employeeDTO.setLastName("Doe");
		    employeeDTO.setEmail("john@gmail.com");
		    employeeDTO.setPhone("9876543210");
		    employeeDTO.setDepartment("IT");
		    employeeDTO.setDesignation("Software Developer");
		    employeeDTO.setSalary(50000.0);
		    
		    when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(employeeDTO);
		    
		    mockMvc.perform(post("/api/employees")
		    		.contentType(APPLICATION_JSON)
		    		.content(objectMapper.writeValueAsString(employeeDTO))
		    		)
		    .andExpect(status().isCreated())
		    .andExpect(jsonPath("$.success").value(true))
		    .andExpect(jsonPath("$.message").value("Employee created successfully"))
		    .andExpect(jsonPath("$.data.firstName").value("John"))
		    .andExpect(jsonPath("$.data.email").value("john@gmail.com"));
	}
	
	@Test
	void createEmployee_shouldReturn400_whenValidationFails() throws Exception {

	    EmployeeDTO employeeDTO = new EmployeeDTO();

	    employeeDTO.setFirstName("");
	    employeeDTO.setLastName("");
	    employeeDTO.setEmail("invalid-email");
	    employeeDTO.setPhone("123");
	    employeeDTO.setDepartment("");
	    employeeDTO.setDesignation("");
	    employeeDTO.setSalary(-100.0);

	    mockMvc.perform(
	            post("/api/employees")
	                    .contentType(APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(employeeDTO))
	        )
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.firstName")
	                .value("First name is required"))
	        .andExpect(jsonPath("$.lastName")
	                .value("Last name is required"))
	        .andExpect(jsonPath("$.email")
	                .value("Please provide a valid email address"))
	        .andExpect(jsonPath("$.phone")
	                .value("Phone number must contain exactly 10 digits"))
	        .andExpect(jsonPath("$.department")
	                .value("Department is required"))
	        .andExpect(jsonPath("$.designation")
	                .value("Designation is required"))
	        .andExpect(jsonPath("$.salary")
	                .value("Salary cannot be negative"));
	}
	
	@Test
	void updateEmployee_shouldReturn200_whenEmployeeIsUpdated() throws Exception {

	    EmployeeDTO employeeDTO = new EmployeeDTO();
	    employeeDTO.setId(1L);
	    employeeDTO.setFirstName("John");
	    employeeDTO.setLastName("Updated");
	    employeeDTO.setEmail("john.updated@gmail.com");
	    employeeDTO.setPhone("9876543210");
	    employeeDTO.setDepartment("IT");
	    employeeDTO.setDesignation("Senior Software Developer");
	    employeeDTO.setSalary(70000.0);

	    when(employeeService.updateEmployee(org.mockito.ArgumentMatchers.eq(1L),
	    		org.mockito.ArgumentMatchers.any(EmployeeDTO.class)))
	            .thenReturn(employeeDTO);

	    mockMvc.perform(
	            put("/api/employees/1")
	                    .contentType(APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(employeeDTO))
	        )
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$.success").value(true))
	        .andExpect(jsonPath("$.message")
	                .value("Employee updated successfully"))
	        .andExpect(jsonPath("$.data.id").value(1))
	        .andExpect(jsonPath("$.data.firstName")
	                .value("John"))
	        .andExpect(jsonPath("$.data.lastName")
	                .value("Updated"))
	        .andExpect(jsonPath("$.data.email")
	                .value("john.updated@gmail.com"))
	        .andExpect(jsonPath("$.data.designation")
	                .value("Senior Software Developer"))
	        .andExpect(jsonPath("$.data.salary")
	                .value(70000.0));
	}
	
	@Test
	void updateEmployee_shouldReturn404_whenEmployeeDoesNotExist() throws Exception {

	    EmployeeDTO employeeDTO = new EmployeeDTO();
	    employeeDTO.setFirstName("John");
	    employeeDTO.setLastName("Doe");
	    employeeDTO.setEmail("john.updated@gmail.com");
	    employeeDTO.setPhone("9876543210");
	    employeeDTO.setDepartment("IT");
	    employeeDTO.setDesignation("Software Developer");
	    employeeDTO.setSalary(50000.0);

	    when(employeeService.updateEmployee(
	            eq(999L),
	            any(EmployeeDTO.class)
	    )).thenThrow(
	            new EmployeeNotFoundException(
	                    "Employee not found with id: 999"
	            )
	    );

	    mockMvc.perform(
	            put("/api/employees/999")
	                    .contentType(APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(employeeDTO))
	    )
	    .andExpect(status().isNotFound())
	    .andExpect(jsonPath("$.success").value(false))
	    .andExpect(jsonPath("$.message")
	            .value("Employee not found with id: 999"))
	    .andExpect(jsonPath("$.data").isEmpty());
	}
	
	@Test
	void deleteEmployee_shouldReturn200_whenEmployeeIsDeleted() throws Exception {
		
		mockMvc.perform(delete("/api/employees/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("Employee deleted successfully"))
		.andExpect(jsonPath("$.data").isEmpty());
	}
	
	@Test
	void deleteEmployee_shouldReturn404_whenEmployeeDoesNotExist() throws Exception {
		
		doThrow(
				new EmployeeNotFoundException(
						"Employee not found with id: 999"
						)
				).when(employeeService).deleteEmployee(999L);
		mockMvc.perform(delete("/api/employees/999"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.success").value(false))
		.andExpect(jsonPath("$.message").value("Employee not found with id: 999"))
		.andExpect(jsonPath("$.data").isEmpty());
		
	}
}
