package com.ourhour.global.jwt.mapper;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtClaimMapper {

    // JWT Parsing (JWT token -> Claim(Payload) 매핑)
    public io.jsonwebtoken.Claims getJwtClaims(SecretKey secretKey, String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
