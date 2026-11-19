package com.nooman.employee_management_system.security;

import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey signingKey;
	private final long expirationMs;
	
	
	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
		
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
		this.expirationMs = expirationMs;
	}
	
	public String generateToken(String email, String role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);
		
		return Jwts.builder()
				.subject(email)
				.claim("role", role)
				.issuedAt(now)
				.expiration(expiry)
				.signWith(signingKey)
				.compact();
	}
	
	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}
	
	public boolean isTokenValid(String token, String email) {
		Claims claims = extractAllClaims(token);
		
		return email.equals(claims.getSubject())
		&& claims.getExpiration().after(new Date());
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
