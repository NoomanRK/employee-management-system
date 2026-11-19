package com.nooman.employee_management_system.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nooman.employee_management_system.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http) throws Exception {
		
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(
					corsConfigurationSource()
			))
			.sessionManagement(session -> session.sessionCreationPolicy(
					SessionCreationPolicy.STATELESS
			)
		)
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				
				//Both USER and ADMIN can view employees
				.requestMatchers(HttpMethod.GET, "/api/employees/**")
				.hasAnyRole("USER", "ADMIN")
				
				//Only ADMIN can modify employee records
				.requestMatchers(HttpMethod.POST, "/api/employees",
						"/api/employees/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/employees/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				)
		.httpBasic(basic -> basic.disable())
		.formLogin(form -> form.disable())
		.exceptionHandling(exception -> exception
				.authenticationEntryPoint(
						new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied");
				}))
		.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOrigins(
				List.of("http://localhost:5173","http://localhost:5174")
				
				);
		
		configuration.setAllowedMethods(
				List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
				);
		
		configuration.setAllowedHeaders(
				List.of("Authorization", "Content-Type")
				);
		
		UrlBasedCorsConfigurationSource source = 
				new UrlBasedCorsConfigurationSource();
		
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
}
