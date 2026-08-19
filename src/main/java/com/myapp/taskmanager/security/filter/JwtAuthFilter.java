package com.myapp.taskmanager.security.filter;

import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.security.service.JwtService;
import com.myapp.taskmanager.security.service.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtAuthFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain   // la cadena de filtros siguiente
    ) throws ServletException, IOException {

        // El token viene en el header "Authorization: Bearer <token>"
        final String authHeader = request.getHeader("Authorization");

        // Si no hay Header o no empieza con "Bearer", lo dejamos pasar sin autenticar
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response); // pasa al siguiente filtro
        }

        // Extraer el token removiendo "Bearer..."
        final String jwt = authHeader.substring(7);

        // Verificamos blacklist ANTES de validar el token
        // Si esta en BLACKLIST, rechazamos inmediatamente
        if(tokenBlacklistService.isBlacklisted(jwt)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":401,\"message\":\"Token Invalidated\"}"
            );
            return;
        }

        final String email;
        try{
            email = jwtService.extractEmail(jwt);
        }catch(JwtException e){
            filterChain.doFilter(request, response);
            return;
        }
        // Si hay un email y aun no esta autenticado en este contexto
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // Buscamos el usuario en la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            try{
                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                    // Creamos el objeto o elemento para que Spring Security lo entienda
                    UsernamePasswordAuthenticationToken authToken = new
                            UsernamePasswordAuthenticationToken(
                                    userDetails,
                            null,
                            userDetails.getAuthorities() // Roles-Permisos
                    );
                    authToken.setDetails(new
                            WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }catch (JwtException e){
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
