package com.nooman.employee_management_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nooman.employee_management_system.entity.Employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import com.nooman.employee_management_system.dto.SalaryOverviewDTO;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Optional<Employee> findByEmail(String email);
	Optional<Employee> findByEmailAndIdNot(String email, Long id);
	
	boolean existsByEmail(String email);
	
	Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrDesignationContainingIgnoreCase(
			String firstName,
			String lastName,
			String email,
			String department,
			String designation,
			Pageable pageable
			);
	
	@Query("""
		    SELECT e.department, COUNT(e)
		    FROM Employee e
		    GROUP BY e.department
		""")
		List<Object[]> countEmployeesByDepartment();
		
		@Query("SELECT AVG(e.salary) FROM Employee e")
		Double findAverageSalary();
		
		@Query("""
			    SELECT new com.nooman.employee_management_system.dto.SalaryOverviewDTO(
			        CONCAT(CONCAT(e.firstName, ' '), e.lastName),
			        e.salary
			    )
			    FROM Employee e
			    ORDER BY e.salary DESC
			""")
			List<SalaryOverviewDTO> findAllEmployeeSalaries();
}
