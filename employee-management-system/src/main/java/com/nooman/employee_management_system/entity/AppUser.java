package com.nooman.employee_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(
		name = "app_users",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_app_users_email",
						columnNames = "email"
						)
		}
		)
public class AppUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email")
	@Column(nullable = false, unique = true)
	private String email;
	
	@NotBlank(message = "Password is required")
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role = "USER";
	
	public AppUser() {

	}
	
	public AppUser(String email, String password, String role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
}
