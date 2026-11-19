package com.nooman.employee_management_system.service;

import org.springframework.stereotype.Service;

import com.nooman.employee_management_system.dto.EmployeeDTO;
import com.nooman.employee_management_system.entity.Employee;
import com.nooman.employee_management_system.repository.EmployeeRepository;
import com.nooman.employee_management_system.mapper.EmployeeMapper;
import com.nooman.employee_management_system.exception.EmailAlreadyExistsException;
import com.nooman.employee_management_system.exception.EmployeeNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
    	
    	if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
    		throw new EmailAlreadyExistsException(
    				"Employee with email " + employeeDTO.getEmail() 
    				+ " already exists");
    	}
    	
    	Employee employee = EmployeeMapper.toEntity(employeeDTO);
    	
    	Employee savedEmployee = employeeRepository.save(employee);
    	
        return EmployeeMapper.toDTO(savedEmployee);
    }

    @Override
    public Page<EmployeeDTO> getAllEmployees(
    		String search, 
    		int page, 
    		int size,
    		String sortBy,
    		String sortDir) {
    	
    	Sort sort = sortDir.equalsIgnoreCase("desc") 
    			? Sort.by(sortBy).descending()
    			: Sort.by(sortBy).ascending();
    	
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<Employee> employees;
    	
    	if (search == null || search.isBlank()) {   
    		
    		employees = employeeRepository.findAll(pageable);
    		
    	} else {    		
    		employees = employeeRepository
    				.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrDesignationContainingIgnoreCase(
    						search,
    						search,
    						search,
    						search,
    						search,
    						pageable    						
    					);
    	}
    	
        return employees.map(EmployeeMapper::toDTO);
        		
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
    	
    	Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with id: " + id));
    	
    	return EmployeeMapper.toDTO(employee);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {

        Employee existingEmployee = employeeRepository.findById(id)
        		.orElseThrow(() -> 
        				new EmployeeNotFoundException( 
        						"Employee not found with id: " + id)
        				);
        
        if (employeeRepository
        		.findByEmailAndIdNot(employeeDTO.getEmail(), id)
        		.isPresent()) {
        	
        	throw new EmailAlreadyExistsException(
        			"Employee with email " + employeeDTO.getEmail()
        			+ " already exists"
        			);
        }

        existingEmployee.setFirstName(employeeDTO.getFirstName());
        existingEmployee.setLastName(employeeDTO.getLastName());
        existingEmployee.setEmail(employeeDTO.getEmail());
        existingEmployee.setPhone(employeeDTO.getPhone());
        existingEmployee.setDepartment(employeeDTO.getDepartment());
        existingEmployee.setDesignation(employeeDTO.getDesignation());
        existingEmployee.setSalary(employeeDTO.getSalary());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        
        return EmployeeMapper.toDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
    	Employee employee = employeeRepository.findById(id)
    			.orElseThrow(() ->
    			new EmployeeNotFoundException(
    			"Employee not found with id: " + id)
    			);
    	
    	employeeRepository.delete(employee);
    }
}