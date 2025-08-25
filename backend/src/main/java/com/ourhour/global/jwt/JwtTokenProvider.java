package com.ourhour.global.jwt;

import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.jwt.mapper.JwtClaimMapper;
import com.ourhour.global.jwt.mapper.OrgAuthorityMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {


    private final SecretKey secretKey; // JWT 서명에 사용할 signature
    private final OrgAuthorityMapper orgAuthorityMapper;
    private final JwtClaimMapper jwtClaimMapper;

    // Acess Token 만료시간 (분)
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    // Refresh Token 만료시간 (일)
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    // SSE Token 만료시간 (5분)
    private static final long SSE_TOKEN_VALIDITY_IN_SECONDS = 5 * 60;

    // Access Token 생성
    public String generateAccessToken(Claims claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(claims.getUserId()))
                .claim("userId", claims.getUserId())
                .claim("email", claims.getEmail())
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
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS512) // 64바이트 알고리즘
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Claims claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(claims.getUserId()))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // SSE 전용 토큰 생성 (5분 유효기간, 최소 정보만 포함)
    public String generateSseToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + SSE_TOKEN_VALIDITY_IN_SECONDS * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("tokenType", "SSE")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // JWT Claim(payload)에서 Access Token Custom Claims 추출
    public Claims parseAccessToken(String token) {

        io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);

        List<Map<String, Object>> orgAuthoritiesRaw = jwtClaims.get("orgAuthorityList", List.class);

        return Claims.builder()
                .email((String) jwtClaims.get("email"))
                .userId(((Number) jwtClaims.get("userId")).longValue())
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

    // JWT Claim(payload)에서 SSE Token Custom Claims 추출
    public Claims parseSseToken(String token) {

        io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);

        return Claims.builder()
                .userId(((Number) jwtClaims.get("userId")).longValue())
                .build();
    }

    // 토큰에서 생성 시간 추출
    public LocalDateTime getIatFromToken(String token) {

        io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);
        Date issuedAtDate = jwtClaims.getIssuedAt();

        return issuedAtDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // 토큰에서 만료 시간 추출
    public LocalDateTime getExpFromToken(String token) {

        io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);
        Date expiredAt = jwtClaims.getExpiration();

        return expiredAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;

        try {
            jwtClaimMapper.getJwtClaims(secretKey, token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // SSE 토큰 타입 검증
    public boolean isSseToken(String token) {
        if (!validateToken(token)) return false;

        try {
            io.jsonwebtoken.Claims jwtClaims = jwtClaimMapper.getJwtClaims(secretKey, token);
            return "SSE".equals(jwtClaims.get("tokenType"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT → Claims → CustomUserDetails → Authentication 변환
    public Authentication getAuthenticationFromToken(String token) {
        // SSE 토큰인 경우 별도 처리
        if (isSseToken(token)) {
            return getAuthenticationFromSseToken(token);
        }

        // JWT(Access Token) -> Claims 추출
        Claims claims = parseAccessToken(token);

        // 인가를 위한 Role enum -> string -> GrantedAuthority 변환
        List<SimpleGrantedAuthority> authorities = claims.getOrgAuthorityList().stream()
                .map(auth -> new SimpleGrantedAuthority("ROLE_" + auth.getRole().name()))
                .toList();

        // Claims -> CustomUserDetails 객체 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                claims.getUserId(),
                claims.getEmail(),
                null,
                claims.getOrgAuthorityList(),
                authorities
        );

        // Security Context에 등록할 CustomUserDetails -> Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        return authentication;
    }

    // SSE 토큰 → Authentication 변환 (최소 권한으로 설정)
    public Authentication getAuthenticationFromSseToken(String sseToken) {
        // SSE 토큰 -> Claims 추출
        Claims claims = parseSseToken(sseToken);

        // SSE용 최소 권한 설정
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_SSE_USER")
        );

        // SSE용 CustomUserDetails 객체 생성 (최소 정보만)
        CustomUserDetails userDetails = new CustomUserDetails(
                claims.getUserId(),
                null, // email 불포함
                null,
                List.of(), // orgAuthorityList 빈 리스트
                authorities
        );

        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

}
