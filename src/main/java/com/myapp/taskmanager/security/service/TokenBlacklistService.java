package com.myapp.taskmanager.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Agrega un token a la blacklist con TTL igual al timepo restante del token
    public void blackListToken(String token, long remainingTimeMs){
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(
                key,
                "invalidated",  // el valor no importa, solo la existencia de la key
                remainingTimeMs,
                TimeUnit.MILLISECONDS   // Redis borra la key automaticamente cuando expira
        );
    }

    // Verifica si el token esta en la blacklist
    public boolean isBlacklisted(String token){
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
