package com.myapp.taskmanager.security.filter;

import com.myapp.taskmanager.repository.UserRepository;
import com.myapp.taskmanager.security.service.JwtService;
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

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain   // la cadena de filtros siguiente
    ) throws ServletException, IOException {

        // El token viene en el header "Authorization: Bearer <token>"
        final String authHeader = request.getHeader("Authorization");

        // Si no hay Hedaer o no empieza con "Bearer", lo dejamos pasar sin autenticar
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response); // pasa al siguiente filtro
        }

        // Extraer el token removiendo "Bearer..."
        final String jwt = authHeader.substring(7);
        final String email = jwtService.extractEmail(jwt);

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
