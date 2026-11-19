package com.nooman.employee_management_system.dto;

public class SalaryOverviewDTO {

	private String employeeName;
	private Double salary;
	
	public SalaryOverviewDTO(String employeeName, Double salary) {
		this.employeeName = employeeName;
		this.salary = salary;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	
	public Double getSalary() {
		return salary;
	}
}
