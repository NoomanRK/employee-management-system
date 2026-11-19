package com.nooman.employee_management_system.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nooman.employee_management_system.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmployeeNotFound(
			EmployeeNotFoundException exception) {
		
		ApiResponse<Void> response = new ApiResponse<>(
				false, 
				exception.getMessage(),
				null);
		
		
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(response);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(
			MethodArgumentNotValidException exception) {
		
		Map<String, String> errors = new HashMap<>();
		
		exception.getBindingResult()
				.getFieldErrors()
				.forEach(error -> 
						errors.put(
								error.getField(),
								error.getDefaultMessage()	
						)
				);
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(errors);
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExists(
			EmailAlreadyExistsException exception) {
		
		ApiResponse<Void> response = new ApiResponse<>(
				false,
				exception.getMessage(),
				null
			);
		
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(response);
	}
}
