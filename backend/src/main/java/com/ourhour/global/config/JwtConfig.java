package com.ourhour.global.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecretKey secretKey() {
        // Base64로 인코딩된 문자열 -> 바이트 배열로 디코딩
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        // 디코딩된 바이트 배열 -> SecretKey 객체 반환(HMAC SHA 알고리즘 사용)
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
