package com.ourhour.domain.auth;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.OrgAuthority;
import com.ourhour.global.jwt.mapper.JwtClaimMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class JwtTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtClaimMapper jwtClaimMapper;

    private Claims claims;

    @BeforeEach
    void setUp() {
        claims = Claims.builder()
                .email("test@example.com")
                .userId(1L)
                .activeOrgId(100L)
                .orgAuthorityList(List.of(
                        new OrgAuthority(100L, 1L, Role.ADMIN),
                        new OrgAuthority(101L, 1L, Role.MEMBER)
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
        assertThat(payloadFromAccessToken.getActiveOrgId()).isEqualTo(claims.getActiveOrgId());
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
