package com.myapp.taskmanager.controller;

import com.myapp.taskmanager.dto.request.UserRequestDTO;
import com.myapp.taskmanager.dto.response.UserResponseDTO;
import com.myapp.taskmanager.entity.RefreshToken;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.security.dto.LoginResponseDTO;
import com.myapp.taskmanager.security.dto.LoginRequestDTO;
import com.myapp.taskmanager.security.dto.RefreshTokenRequestDTO;
import com.myapp.taskmanager.security.service.JwtService;
import com.myapp.taskmanager.security.service.RefreshTokenService;
import com.myapp.taskmanager.security.service.TokenBlacklistService;
import com.myapp.taskmanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    public AuthController(UserService userService,
                          UserRepository userRepository,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService,
                          AuthenticationManager authenticationManager, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // POST: /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserRequestDTO requestDTO
    ){
        UserResponseDTO created = userService.createUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // POST: /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ){
        // authenticate() -> verifica email + contraseña con AuthenticationProvider
        // si las credenciales son incorrectas lanza AuthorizationException -> 401 automatico
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        // Si llegamos acá, las credenciales son válidas
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow();

        String accessToken = jwtService.generateToken(Map.of(), user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(
                accessToken,
                refreshToken.getToken(),
                user.getEmail(),
                user.getName(),
                jwtExpiration
        ));
    }

    // Endpoint para renovar el access token
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ) {
        RefreshToken refreshToken = refreshTokenService
                .validateRefreshToken(requestDTO.getRefreshToken());

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(Map.of(), user.getEmail());

        return ResponseEntity.ok(new LoginResponseDTO(
                newAccessToken,
                refreshToken.getToken(), // mismo refresh token, no se rota aca
                user.getEmail(),
                user.getName(),
                jwtExpiration
        ));
    }

    // Logout: invalida el refresh token
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal User currentUser,
            HttpServletRequest request
    ) {
        // Extraemos el token del header
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // traemos el calculo de tiempo del mismo service, debido a que el metodo principal es privado
            long remainingTime = jwtService.getRemainingTime(token);

            // Si todavia no expiro, lo agregamos a blacklist
            if(remainingTime > 0){
                tokenBlacklistService.blackListToken(token, remainingTime);
            }
        }
        // Borramos el refresh token de la BD
        refreshTokenService.deleteByUser(currentUser);

        return ResponseEntity.noContent().build();
    }
}
