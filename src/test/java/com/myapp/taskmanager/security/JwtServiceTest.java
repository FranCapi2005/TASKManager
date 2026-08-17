// test/security/JwtServiceTest.java
package com.myapp.taskmanager.security;

import com.myapp.taskmanager.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos los @Value manualmente en tests unitarios
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "test-clave-secreta-para-tests-minimo-32-chars!!");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    @DisplayName("generateToken → genera token no vacío")
    void generateToken_ReturnsNonEmptyToken() {
        String token = jwtService.generateToken(Map.of(),"test@test.com");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractEmail → extrae email correctamente del token")
    void extractEmail_ReturnsCorrectEmail() {
        String token = jwtService.generateToken(Map.of(),"test@test.com");
        String email = jwtService.extractEmail(token);
        assertThat(email).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("isTokenValid → token válido → retorna true")
    void isTokenValid_WithValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(Map.of(),"test@test.com");
        assertThat(jwtService.isTokenValid(token, "test@test.com")).isTrue();
    }

    @Test
    @DisplayName("isTokenValid → email diferente → retorna false")
    void isTokenValid_WithWrongEmail_ReturnsFalse() {
        String token = jwtService.generateToken(Map.of(),"test@test.com");
        assertThat(jwtService.isTokenValid(token, "otro@test.com")).isFalse();
    }
}