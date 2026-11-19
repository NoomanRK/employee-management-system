package com.nooman.employee_management_system.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nooman.employee_management_system.dto.LoginRequest;
import com.nooman.employee_management_system.dto.RegisterRequest;
import com.nooman.employee_management_system.entity.AppUser;
import com.nooman.employee_management_system.repository.AppUserRepository;
import com.nooman.employee_management_system.security.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	
	public AuthService(
			AppUserRepository appUserRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}
	
	@Transactional
	public String register(RegisterRequest request) {
		
		if(appUserRepository.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Email is already registered"
					);
		}
		
		if(request.getPassword().getBytes(
				java.nio.charset.StandardCharsets.UTF_8).length > 72) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Password must not exceed 72 UTF-8 bytes");
		}
		
		AppUser user = new AppUser();
		user.setEmail(request.getEmail().trim().toLowerCase());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		user.setRole("USER");
		
		appUserRepository.save(user);
		
		return jwtService.generateToken(user.getEmail(), user.getRole());
	}
	
	public String login(LoginRequest request) {
		
		AppUser user = appUserRepository
				.findByEmail(request.getEmail().trim().toLowerCase())
				.orElseThrow(() -> new BadCredentialsException(
						"Invalid email or password"));
		
		if (!passwordEncoder.matches(
				request.getPassword(),
				user.getPassword())) {
			throw new BadCredentialsException(
					"Invalid email or password"
					);
		}
		
		return jwtService.generateToken(user.getEmail(), user.getRole());
	}
}
