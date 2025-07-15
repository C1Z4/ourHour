package com.ourhour.domain.auth;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.OrgAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    private Claims claims;

    @BeforeEach
    void setUp() {

        claims = Claims.builder()
                .email("test@example.com")
                .userId(1L)
                .orgAuthorityList(List.of(
                        new OrgAuthority(100L, 1L, Role.ADMIN),
                        new OrgAuthority(101L, 1L, Role.MEMBER)
                ))
                .build();

    }

    @DisplayName("인증 필터 테스트 - 토큰 없을 경우(401: Unauthorized)")
    @Test
    void authenticationFailTest() throws Exception {

         mockMvc.perform(get("/api/test/auth-check"))
                .andExpect(status().isUnauthorized());

    }

    @DisplayName("인증 필터 테스트 - 토큰 있는 경우(200: ok)")
    @Test
    void authenticationSuccessTest() throws Exception {

        String accessToken = jwtTokenProvider.generateAccessToken(claims);

        mockMvc.perform(get("/api/test/auth-check")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 성공"));

    }

    @DisplayName("인가 테스트 - 권한이 충분하지 않은 경우 (403: forbidden)")
    @Test
    void authorizationFailTest() throws Exception {

        String accessToken = jwtTokenProvider.generateAccessToken(claims);

        mockMvc.perform(get("/api/test/access-check/{orgId}", 101)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());

    }

    @DisplayName("인가 테스트 - 권한이 충분할 경우(200: ok)")
    @Test
    void authorizationSuccessTest() throws Exception {
        String accessToken = jwtTokenProvider.generateAccessToken(claims);

        mockMvc.perform(get("/api/test/access-check/{orgId}", 100)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

    }

}
