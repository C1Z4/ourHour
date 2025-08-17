package com.ourhour.global.jwt.filter;

import com.ourhour.global.constant.AuthPath;
import com.ourhour.global.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        boolean isPublic = Arrays.stream(AuthPath.PUBLIC_URLS)
                .anyMatch(requestURI::startsWith);

        boolean isSwagger = Arrays.stream(AuthPath.SWAGGER_URLS)
                .anyMatch(requestURI::startsWith);

        // 비인증 요청 및 스웨거 필터 스킵
        if (isPublic || isSwagger) {
            filterChain.doFilter(request, response);

            return;
        }

        // 민감 정보 응답에 대한 캐시 방지 헤더 추가
        if (requestURI.startsWith("/api/auth") || requestURI.startsWith("/api/user")) {
            response.setHeader("Cache-Control", "no-store");
        }

        // HttpRequest -> token 추출
        String token = getToken(request);

        // 토큰 유효성 검사
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // token -> Authentication 객체 파싱
        Authentication authentication = jwtTokenProvider.getAuthenticationFromToken(token);

        // Authentication 객체 유효성 검사
        if (authentication == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 정상 토큰이면 SecurityContext 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    // request header => Authorization: Bearer accessToken
    private String getToken(HttpServletRequest request) {

        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        return null;
    }

}
