package com.myapp.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Acá irá configuración de serialización, formatos de fecha, etc.
    // Lo expandimos cuando sea necesario
}