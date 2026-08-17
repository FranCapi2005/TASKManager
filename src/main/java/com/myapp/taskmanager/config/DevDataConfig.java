package com.myapp.taskmanager.config;

import com.myapp.taskmanager.entity.Role;
import com.myapp.taskmanager.entity.User;
import com.myapp.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

// Ejemplo: la consola H2 solo en dev, un Bean de datos de prueba solo en dev
@Configuration
@Profile("dev")  // este Bean SOLO se crea si el perfil activo es "dev"
public class DevDataConfig {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Usuario ADMIN
                User admin = new User();
                admin.setName("Admin Dev");
                admin.setEmail("admin@dev.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                // Usuario NORMAL
                User user = new User();
                user.setName("User Dev");
                user.setEmail("user@dev.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(Role.USER);
                userRepository.save(user);

                System.out.println(">>> Admin: admin@dev.com / admin123");
                System.out.println(">>> User:  user@dev.com  / user123");
            }
        };
    }
}
