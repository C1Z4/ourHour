package com.ourhour.domain.auth;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.OrgAuthority;
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

    @DisplayName("JWT 방식으로 AccessToken과 RefreshToken 생성 테스트")
    @Test
    void generateTokenTest() {

        // given: 테스트용 claim
        Claims claims = new Claims(
                "test@example.com",  // email
                1L,                        // userId
                200L,                      // activeOrgId
                List.of(                   // orgAuthorityList
                        new OrgAuthority(200L, 100L, Role.ADMIN),
                        new OrgAuthority(201L, 101L, Role.MEMBER)
                )
        );

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

        // given: 테스트용 claim
        Claims claims = new Claims(
                "test@example.com",  // email
                1L,                        // userId
                200L,                      // activeOrgId
                List.of(                   // orgAuthorityList
                        new OrgAuthority(200L, 100L, Role.ADMIN),
                        new OrgAuthority(201L, 101L, Role.MEMBER)
                )
        );
        String accessToken = jwtTokenProvider.generateAccessToken(claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(claims);

        // when
        Claims payloadFromAccessToken = jwtTokenProvider.parseAcessToken(accessToken);
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

        // given: 테스트용 claim
        Claims claims = new Claims(
                "test@example.com",  // email
                1L,                        // userId
                200L,                      // activeOrgId
                List.of(                   // orgAuthorityList
                        new OrgAuthority(200L, 100L, Role.ADMIN),
                        new OrgAuthority(201L, 101L, Role.MEMBER)
                )
        );
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
