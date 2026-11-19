package com.nooman.employee_management_system.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nooman.employee_management_system.entity.AppUser;
import com.nooman.employee_management_system.repository.AppUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private final AppUserRepository appUserRepository;
	
	public CustomUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() ->
				
							new UsernameNotFoundException(
									"User not found with email: " + email
									)
							);
		
		return User.builder()
				.username(appUser.getEmail())
				.password(appUser.getPassword())
				.roles(appUser.getRole())
				.build();
	}
}
