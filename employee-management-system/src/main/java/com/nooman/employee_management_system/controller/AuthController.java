package com.nooman.employee_management_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nooman.employee_management_system.dto.AuthResponse;
import com.nooman.employee_management_system.dto.LoginRequest;
import com.nooman.employee_management_system.dto.RegisterRequest;
import com.nooman.employee_management_system.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(
			@Valid @RequestBody RegisterRequest request) {
		
		System.out.println("REGISTER ENDPOINT REACHED");
		
		String token = authService.register(request);
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new AuthResponse(token, "Bearer"));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@Valid @RequestBody LoginRequest request) {
		
		String token = authService.login(request);
		
		return ResponseEntity.ok(
				new AuthResponse(token, "Bearer")
				);
	}
}
