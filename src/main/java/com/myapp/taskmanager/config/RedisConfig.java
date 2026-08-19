package com.myapp.taskmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching      // Activa el sistema de cache de Spring en toda la app
public class RedisConfig {

    // ObjectMapper configurado para Redis
    @Bean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Soporte para LocalDateTime, Instant y demas tipos de java.time
        mapper.registerModule(new JavaTimeModule());

        // Serializa fechas como String ISO en vez de array numerico
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    // Configurar como se guardan los datos en Redis
    @Bean
    public RedisCacheConfiguration cacheConfiguration(){
        // Sin ObjectMapper personalizado, RedisSerializer.json()
        RedisSerializer<Object> serializer = RedisSerializer.json();

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))   // TTL default: 10m
                .disableCachingNullValues()
                .serializeKeysWith(
                        // las llaves se guardan como Strings legibles
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        // los valores se guardan como JSON legible
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer) // constructor con ObjectMapper
                );
    }
    // RedisTemplate: herramienta para operaciones manuales en Redis
    // Lo usamos para la blacklist de JWT
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // keys y values como String simple
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
