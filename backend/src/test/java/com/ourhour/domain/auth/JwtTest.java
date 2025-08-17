package com.ourhour.domain.auth;

import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.JwtTokenProvider;

import com.ourhour.global.jwt.mapper.JwtClaimMapper;
import com.ourhour.global.jwt.mapper.OrgAuthorityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.Jwts;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
class JwtTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtClaimMapper jwtClaimMapper;
    private Claims claims;

    @BeforeEach
    void setUp() {

        // 단위 테스트용 SecretKey (임의)
        SecretKey secretKey = new SecretKeySpec("thisissecretthisissecretthisissecretthisissecretthisissecretthisissecretthisissecretthisissecret".getBytes(StandardCharsets.UTF_8), "HmacSHA512");

        jwtClaimMapper = new JwtClaimMapper() {
            @Override
            public io.jsonwebtoken.Claims getJwtClaims(SecretKey key, String token) {
                return Jwts.parser()
                        .verifyWith(key)
                        .setAllowedClockSkewSeconds(60)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            }
        };

        // OrgAuthorityMapper mock으로 만들어서 주입
        OrgAuthorityMapper orgAuthorityMapper = mock(OrgAuthorityMapper.class);

        jwtTokenProvider = new JwtTokenProvider(secretKey, orgAuthorityMapper, jwtClaimMapper);

        claims = Claims.builder()
                .email("test@example.com")
                .userId(1L)
                .orgAuthorityList(List.of(
                ))
                .build();
    }

    @DisplayName("JWT 방식으로 AccessToken과 RefreshToken 생성 테스트")
    @Test
    void generateTokenTest() {

        // when
        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);

        // then
        assertThat(accessToken).isNotNull();

        assertThat(refreshToken).isNotNull();

    }

    @DisplayName("생성한 JWT token -> Custom claim으로 parsing 테스트")
    @Test
    void parseTokenTest() {

        // given
        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        // when
        Claims payloadFromAccessToken = jwtTokenProvider.parseAccessToken(accessToken);
        Claims payloadFromRefreshToken = jwtTokenProvider.parseRefreshToken(refreshToken);

        // then
        assertThat(payloadFromAccessToken).isNotNull();
        assertThat(payloadFromAccessToken.getOrgAuthorityList()).isNotNull();
        assertThat(payloadFromAccessToken.getEmail()).isEqualTo(claims.getEmail());
        assertThat(payloadFromAccessToken.getUserId()).isEqualTo(claims.getUserId());
        assertThat(payloadFromAccessToken.getOrgAuthorityList().size()).isEqualTo(claims.getOrgAuthorityList().size());

        assertThat(payloadFromRefreshToken.getUserId()).isEqualTo(claims.getUserId());

    }

    @DisplayName("JWT token 유효성 검사 테스트")
    @Test
    void validateToken() {

        // given
        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        // when
        boolean isValidAccessToken = jwtTokenProvider.validateToken(accessToken);
        boolean isValidRefreshToken = jwtTokenProvider.validateToken(refreshToken);

        // then
        assertThat(isValidAccessToken).isTrue();

        assertThat(isValidRefreshToken).isTrue();

    }

}
