package com.myapp.taskmanager.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Clave secreta desde la application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Tiempo de expiracion del token en ms, desde application.properties
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostConstruct // se ejecuta despues de inyectar las dependencias
    public void validateSecretKey(){
        if(secretKey == null || secretKey.length() < 32)
            throw new IllegalStateException(
                    "jwt.secret should be a secret key of at least 32 characters length" +
                            " (you can use `openssl rand -base64 32` to generate one)"
            );
    }

    // Generar tokens con claims extras
    public String generateToken(Map<String, Object> extraClaims, String email){
        return Jwts.builder()
                .claims(extraClaims)
                .subject(email) // quien es el usuario
                .issuedAt(new Date(System.currentTimeMillis())) // cuando se genero
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // cuando expira
                .signWith(getSigningKey())  // clave de firma
                .compact(); // genera el String del token
    }

    // Verifica si el token es válido para un email dado
    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    // Extrae el email del token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Verifica si el token expiró
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrae cualquier claim del token con una función
    // Claims son los datos guardados dentro del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // verifica la firma
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Convierte la clave secreta String en un objeto SecretKey que usa HMAC-SHA
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
