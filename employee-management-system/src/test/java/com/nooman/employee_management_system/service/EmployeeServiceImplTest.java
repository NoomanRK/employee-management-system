package com.nooman.employee_management_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nooman.employee_management_system.repository.EmployeeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.nooman.employee_management_system.dto.EmployeeDTO;
import com.nooman.employee_management_system.entity.Employee;
import com.nooman.employee_management_system.exception.EmailAlreadyExistsException;

import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;

import com.nooman.employee_management_system.exception.EmployeeNotFoundException;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

	@Mock
	private EmployeeRepository employeeRepository;
	
	@InjectMocks
	private EmployeeServiceImpl employeeService;
	
	@BeforeEach
	void setUp() {
		
	}
	
	@Test
	void createEmployee_shouldCreateEmployee_whenEmailIsUnique() {
		
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setFirstName("John");
		employeeDTO.setLastName("Doe");
		employeeDTO.setEmail("john@gmail.com");
		employeeDTO.setPhone("9861491900");
		employeeDTO.setDesignation("Developer");
		employeeDTO.setDepartment("IT");
		employeeDTO.setSalary(900000.0);
		
		Employee employee = new Employee();
		employee.setId(1L);
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setEmail("john@gmail.com");
		employee.setPhone("9861491900");
		employee.setDesignation("Developer");
		employee.setDepartment("IT");
		employee.setSalary(900000.0);
		
		when(employeeRepository.existsByEmail("john@gmail.com")).thenReturn(false);
		
		when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
		
		EmployeeDTO result = employeeService.createEmployee(employeeDTO);
		
		assertEquals(1L, result.getId());
		assertEquals("John", result.getFirstName());
		assertEquals("john@gmail.com", result.getEmail());
		
		verify(employeeRepository).existsByEmail("john@gmail.com");
		verify(employeeRepository).save(any(Employee.class));
	}
	
	@Test
	void createEmployee_shouldThrowException_whenEmailAlreadyExists() {
		
		EmployeeDTO employeeDTO = new EmployeeDTO();
		
		
		employeeDTO.setId(1L);
		employeeDTO.setFirstName("John");
		employeeDTO.setLastName("Doe");
		employeeDTO.setEmail("john@gmail.com");
		employeeDTO.setPhone("9861491900");
		employeeDTO.setDesignation("Developer");
		employeeDTO.setDepartment("IT");
		employeeDTO.setSalary(900000.0);
		
		when(employeeRepository.existsByEmail("john@gmail.com")).thenReturn(true);
		
		assertThrows(EmailAlreadyExistsException.class, 
				() -> employeeService.createEmployee(employeeDTO));
		
		verify(employeeRepository).existsByEmail("john@gmail.com");
		verify(employeeRepository, never()).save(any(Employee.class));
	}
	
	@Test
	void getEmployeeById_shouldReturnEmployee_whenIdExists() {
		
		Long employeeId = 1L;
		
		Employee employee = new Employee();
		employee.setId(employeeId);
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setEmail("john@gmail.com");
		employee.setPhone("9861491900");
		employee.setDepartment("IT");
		employee.setDesignation("Developer");
		employee.setSalary(9000000.0);
		
		when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.of(employee));
		
		EmployeeDTO result = employeeService.getEmployeeById(employeeId);
		
		assertEquals(employeeId, result.getId());
		assertEquals("John", result.getFirstName());
		assertEquals("john@gmail.com", result.getEmail());
		
		verify(employeeRepository).findById(employeeId);
		
	}
	
	@Test
	void getEmployeeById_shouldThrowException_whenIdDoesNotExists() {
		
		Long employeeId = 999L;
		
		when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.empty());
		
		assertThrows(
				EmployeeNotFoundException.class,
				() -> employeeService.getEmployeeById(employeeId)
		);
		
		verify(employeeRepository).findById(employeeId);
	}
	
	@Test
	void deleteEmployee_shouldDeleteEmployee_whenIdExists() {
		
		Long employeeId = 1L;
		
		Employee employee = new Employee();
		employee.setId(employeeId);
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setEmail("john@gmail.com");
		
		when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.of(employee));
		
		employeeService.deleteEmployee(employeeId);
		
		verify(employeeRepository).findById(employeeId);
		verify(employeeRepository).delete(employee);
	}
	
	@Test
	void deleteEmployee_shouldThrowException_whenIdDoesNotExist() {
		
		Long employeeId = 999L;
		
		when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.empty());
		
		assertThrows(
				EmployeeNotFoundException.class,
				() -> employeeService.deleteEmployee(employeeId)
				);
		
		verify(employeeRepository).findById(employeeId);
		
		verify(employeeRepository, never()).delete(any(Employee.class));
	}
	
	@Test
	void updateEmployee_shouldUpdateEmployee_whenDataIsValid() {
		
		Long employeeId = 1L;
		
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setFirstName("John");
		employeeDTO.setLastName("Doe");
		employeeDTO.setEmail("john.updated@gmail.com");
		employeeDTO.setPhone("9861491900");
		employeeDTO.setDepartment("IT");
		employeeDTO.setDesignation("Senior Developer");
		employeeDTO.setSalary(9000000.0);
		
		Employee existingEmployee = new Employee();
		existingEmployee.setId(employeeId);
		existingEmployee.setFirstName("John");
		existingEmployee.setLastName("Doe");
		existingEmployee.setEmail("john@gmail.com");
		existingEmployee.setPhone("9861491900");
		existingEmployee.setDepartment("IT");
		existingEmployee.setDesignation("Developer");
		existingEmployee.setSalary(9000000.0);
		
		Employee updatedEmployee = new Employee();
		updatedEmployee.setId(employeeId);
		updatedEmployee.setFirstName("John");
		updatedEmployee.setLastName("Doe");
		updatedEmployee.setEmail("john.updated@gmail.com");
		updatedEmployee.setPhone("9861491900");
		updatedEmployee.setDepartment("IT");
		updatedEmployee.setDesignation("Senior Developer");
		updatedEmployee.setSalary(9000000.0);
		
		when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.of(existingEmployee));
		when(employeeRepository.findByEmailAndIdNot("john.updated@gmail.com",employeeId)).thenReturn(java.util.Optional.empty());
		
		when(employeeRepository.save(existingEmployee)).thenReturn(updatedEmployee);
		
		EmployeeDTO result = employeeService.updateEmployee(employeeId, employeeDTO);
		
		assertEquals(employeeId, result.getId());
		assertEquals("John", result.getFirstName());
		assertEquals("john.updated@gmail.com", result.getEmail());
		assertEquals("Senior Developer", result.getDesignation());
		assertEquals(9000000.0, result.getSalary());
		
		verify(employeeRepository).findById(employeeId);
		verify(employeeRepository).findByEmailAndIdNot("john.updated@gmail.com", employeeId);
		verify(employeeRepository).save(existingEmployee);
	}
	
	@Test
	void updateEmployee_shouldThrowException_whenEmployeeDoesNotExist() {
		
		Long employeeId = 999L;
		
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setFirstName("John");
		employeeDTO.setLastName("Doe");
		employeeDTO.setEmail("john.updated@gmail.com");
		employeeDTO.setPhone("9861491900");
		employeeDTO.setDepartment("IT");
		employeeDTO.setDesignation("Developer");
		employeeDTO.setSalary(9000000.0);
		
		when(employeeRepository.findById(employeeId))
		.thenReturn(java.util.Optional.empty());
		
		assertThrows(
				EmployeeNotFoundException.class,
				() -> employeeService.updateEmployee(employeeId, employeeDTO)
				);
		
		verify(employeeRepository).findById(employeeId);
		
		verify(employeeRepository, never()).findByEmailAndIdNot(anyString(), anyLong());
		
		verify(employeeRepository, never()).save(any(Employee.class));
	}
	
	@Test
	void updateEmployee_shouldThrowException_whenEmailAlreadyExists() {
		
		Long employeeId = 1L;
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setFirstName("John");
	    employeeDTO.setLastName("Doe");
	    employeeDTO.setEmail("existing@gmail.com");
	    employeeDTO.setPhone("9876543210");
	    employeeDTO.setDepartment("IT");
	    employeeDTO.setDesignation("Developer");
	    employeeDTO.setSalary(900000.0);

	    Employee existingEmployee = new Employee();
	    existingEmployee.setId(employeeId);
	    existingEmployee.setFirstName("John");
	    existingEmployee.setLastName("Doe");
	    existingEmployee.setEmail("john@gmail.com");
	    existingEmployee.setPhone("9861491900");
	    existingEmployee.setDepartment("IT");
	    existingEmployee.setDesignation("Developer");
	    existingEmployee.setSalary(900000.0);
	    
	    Employee anotherEmployee = new Employee();
	    anotherEmployee.setId(2L);
	    anotherEmployee.setEmail("existing@gmail.com");
	    
	    when(employeeRepository.findById(employeeId)).thenReturn(java.util.Optional.of(existingEmployee));
	    
	    when(employeeRepository.findByEmailAndIdNot("existing@gmail.com", employeeId)).thenReturn(java.util.Optional.of(anotherEmployee));
	    
	    assertThrows(
	    		EmailAlreadyExistsException.class, 
	    		() -> employeeService.updateEmployee(employeeId, employeeDTO)
	    		);
	    
	    verify(employeeRepository).findById(employeeId);
	    
	    verify(employeeRepository).findByEmailAndIdNot(
	    		"existing@gmail.com",
	    		employeeId);
	    
	    verify(employeeRepository, never()).save(any(Employee.class));
	}
	
	@Test
	void getAllEmployees_shouldReturnEmployees_whenSearchIsEmpty() {
		
		int page = 0;
		int size = 5;
		String sortBy = "id";
		String sortDir = "asc";
		
		Employee employee1 = new Employee();
		employee1.setId(1L);
		employee1.setFirstName("John");
		employee1.setLastName("Doe");
		employee1.setEmail("john@gmail.com");
		
		Employee employee2 = new Employee();
		employee2.setId(2L);
		employee2.setFirstName("Unknown");
		employee2.setLastName("Emp");
		employee2.setEmail("unknown@gmail.com");
		
		Pageable pageable = PageRequest.of(
				page,
				size,
				Sort.by(sortBy).ascending()
				);
		
		Page<Employee> employeePage = new PageImpl<>(
				List.of(employee1, employee2),
				pageable,
				2
				);
		
		when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);
		
		Page<EmployeeDTO> result = employeeService.getAllEmployees(
				"",
				page,
				size,
				sortBy,
				sortDir
				
		);
		
		assertEquals(2, result.getContent().size());
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		
		assertEquals("John", result.getContent().get(0).getFirstName());
		assertEquals("Unknown", result.getContent().get(1).getFirstName());
		
		verify(employeeRepository).findAll(any(Pageable.class));
	}
	
	@Test
	void getAllEmployees_shouldReturnMatchingEmployees_whenSearchIsProvided() {
		
		String search = "john";
		int page = 0;
		int size = 5;
		String sortBy = "id";
		String sortDir = "asc";
		
		Employee employee = new Employee();
		employee.setId(1L);
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setEmail("john@gmail.com");
	
		Pageable pageable = PageRequest.of(
				page,
				size,
				Sort.by(sortBy).ascending()
				);
		Page<Employee> employeePage = new PageImpl<>(
				List.of(employee),
				pageable,
				1
				);
		when(employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrDesignationContainingIgnoreCase(
				search,
				search,
				search,
				search,
				search,
				pageable)).thenReturn(employeePage);
		
		Page<EmployeeDTO> result = employeeService.getAllEmployees(
				search,
				page,
				size,
				sortBy,
				sortDir
				);
		
		assertEquals(1, result.getContent().size());
		assertEquals(1, result.getTotalElements());
		assertEquals("John", result.getContent().get(0).getFirstName());
		assertEquals("john@gmail.com", result.getContent().get(0).getEmail());
		
		verify(employeeRepository).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrDesignationContainingIgnoreCase(
				search,
				search,
				search,
				search,
				search,
				pageable
				);
		
		verify(employeeRepository, never()).findAll(any(Pageable.class));
	}
	
	@Test
	void getAllEmployees_shouldSortDescending_whenSortDirectionisDesc() {
		
		String search = "";
		int page = 0;
		int size = 5;
		String sortBy = "salary";
		String sortDir = "desc";
		
		Employee employee1 = new Employee();
		employee1.setId(1L);
		employee1.setFirstName("John");
	    employee1.setLastName("Doe");
	    employee1.setEmail("john@gmail.com");
	    employee1.setSalary(90000.0);
	    
	    Employee employee2 = new Employee();
	    employee2.setId(2L);
	    employee2.setFirstName("Rahul");
	    employee2.setLastName("Sharma");
	    employee2.setEmail("rahul@gmail.com");
	    employee2.setSalary(50000.0);
	    
	    Pageable pageable = PageRequest.of(
	    		page,
	    		size,
	    		Sort.by(sortBy).descending());
	    
	    Page<Employee> employeePage = new PageImpl<>(
	    		List.of(employee1, employee2),
	    		pageable, 
	    		2
	    		);
	    
	    when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);
	    
	    Page<EmployeeDTO> result = employeeService.getAllEmployees(
	    		search,
	    		page,
	    		size,
	    		sortBy,
	    		sortDir
	    		);
	    
	    assertEquals(2, result.getContent().size());
	    assertEquals(
	    		90000.0,
	    		result.getContent().get(0).getSalary()
	    		);
	    assertEquals(50000.0,
	    		result.getContent().get(1).getSalary()
	    		);
	    
	    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
	    verify(employeeRepository).findAll(pageableCaptor.capture());
	    
	    Pageable capturedPageable = pageableCaptor.getValue();
	    
	    assertEquals(0, capturedPageable.getPageNumber());
	    assertEquals(5, capturedPageable.getPageSize());
	    assertEquals(Sort.Direction.DESC,
	    		capturedPageable.getSort().getOrderFor("salary").getDirection());
	    		
	    		verify(employeeRepository).findAll(pageableCaptor.capture());
	    
	    verify(employeeRepository).findAll(any(Pageable.class));
	}
	
}
