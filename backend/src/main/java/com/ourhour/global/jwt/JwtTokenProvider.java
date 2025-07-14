package com.ourhour.global.jwt;

import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.mapper.JwtClaimMapper;
import com.ourhour.global.jwt.mapper.OrgAuthorityMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // JWT 서명에 사용할 signature
    private final SecretKey secretKey;
    private final OrgAuthorityMapper orgAuthorityMapper;
    private final JwtClaimMapper jwtClaimMapper;

    // Acess Token 만료시간 (분)
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    // Refresh Token 만료시간 (일)
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;


    public JwtTokenProvider(@Value("${jwt.secret}") String secret, OrgAuthorityMapper orgAuthorityMapper, JwtClaimMapper jwtClaimMapper) {

        // Base64로 인코딩된 문자열 -> 바이트 배열로 디코딩
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        // 디코딩된 바이트 배열 -> SecretKey 객체를 생성(HMAC SHA 알고리즘 사용)
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.orgAuthorityMapper = orgAuthorityMapper;
        this.jwtClaimMapper = jwtClaimMapper;

    }

    // Access Token 생성
    public String generateAccessToken(Claims claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(claims.getUserId()))
                .claim("email", claims.getEmail())
                .claim("userId", claims.getUserId())
                .claim("activeOrgId", claims.getActiveOrgId())
                .claim("orgAuthorityList",
                     claims.getOrgAuthorityList()
                          .stream().map(
                              orgAuthority -> Map.of(
                                   "orgId", orgAuthority.getOrgId(),
                                   "memberId", orgAuthority.getMemberId(),
                                   "role", orgAuthority.getRole()
                              )
                          ).collect(Collectors.toList())
                )
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS512) // 64바이트 알고리즘
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Claims claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(claims.getUserId()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT Claim(payload)에서 Access Token Custom Claims 추출
    public Claims parseAccessToken(String token) {

        io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);

        List<Map<String, Object>> orgAuthoritiesRaw = jwtClaims.get("orgAuthorityList", List.class);

        return Claims.builder()
                .email((String) jwtClaims.get("email"))
                .userId(((Number) jwtClaims.get("userId")).longValue())
                .activeOrgId(((Number)jwtClaims.get("activeOrgId")).longValue())
                .orgAuthorityList(orgAuthorityMapper.mapListToOrgAuthorityListHelper(orgAuthoritiesRaw))
                .build();
    }

    // JWT Claim(payload)에서 Refresh Token Custom Claims 추출
    public Claims parseRefreshToken(String token) {

        io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);

        return Claims.builder()
                .userId(Long.valueOf(jwtClaims.getSubject()))
                .build();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {

        if (token == null || token.isBlank()) return false;

        try {
            jwtClaimMapper.getJwtClaims(secretKey, token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT : " + e.getMessage());
            return false;
        }

    }

}
