package com.ourhour.domain.auth;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.jwt.dto.OrgAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    private CustomUserDetails adminUser;
    private CustomUserDetails memberUser;

    private String adminToken;
    private String memberToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity()) // Security 필터 적용
                .build();

        // ADMIN 권한 가진 사용자
        adminUser = new CustomUserDetails(
                1L,
                "admin@example.com",
                null,
                List.of(new OrgAuthority(100L, 1L, Role.ADMIN)),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // MEMBER 권한 가진 사용자
        memberUser = new CustomUserDetails(
                2L,
                "member@example.com",
                null,
                List.of(new OrgAuthority(100L, 1L, Role.MEMBER)),
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );

        // JWT 토큰 생성
        adminToken = jwtTokenProvider.generateAccessToken(
                com.ourhour.global.jwt.dto.Claims.builder()
                        .userId(adminUser.getUserId())
                        .email(adminUser.getUsername())
                        .orgAuthorityList(adminUser.getOrgAuthorityList())
                        .build()
        );

        memberToken = jwtTokenProvider.generateAccessToken(
                com.ourhour.global.jwt.dto.Claims.builder()
                        .userId(memberUser.getUserId())
                        .email(memberUser.getUsername())
                        .orgAuthorityList(memberUser.getOrgAuthorityList())
                        .build()
        );
    }

    @DisplayName("인증 테스트 - Security 필터 통과")
    @Test
    void authenticationSuccessTest() throws Exception {
        mockMvc.perform(get("/api/test/auth-check")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 성공"));
    }

    @DisplayName("인가 테스트 - 권한 부족 (403)")
    @Test
    void authorizationFailTest() throws Exception {
        mockMvc.perform(get("/api/test/access-check/{orgId}", 100)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("인가 테스트 - 권한 충분 (200)")
    @Test
    void authorizationSuccessTest() throws Exception {
        mockMvc.perform(get("/api/test/access-check/{orgId}", 100)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("인가 성공"));
    }
}
