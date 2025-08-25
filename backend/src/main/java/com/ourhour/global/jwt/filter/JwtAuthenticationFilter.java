package com.ourhour.global.jwt.filter;

import com.ourhour.global.constant.AuthPath;
import com.ourhour.global.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/ws-stomp/")) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isPublic = Arrays.stream(AuthPath.PUBLIC_URLS)
                .anyMatch(requestURI::startsWith);

        boolean isSwagger = Arrays.stream(AuthPath.SWAGGER_URLS)
                .anyMatch(requestURI::startsWith);

        boolean isStomp = Arrays.stream(AuthPath.STOMP_URLS)
                .anyMatch(requestURI::startsWith);

        boolean isMonitoring = Arrays.stream(AuthPath.MONITORING_URLS)
                .anyMatch(requestURI::startsWith);

        boolean isNotification = Arrays.stream(AuthPath.NOTIFICATION_URLS)
                .anyMatch(requestURI::startsWith);

        // 비인증 요청 스킵
        if (isPublic || isSwagger || isStomp || isMonitoring) {
            filterChain.doFilter(request, response);
            return;
        }

        // SSE 요청의 경우 특별 처리
        if (isNotification) {
            try {
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
            } catch (Exception e) {
                // SSE 요청에서 예외 발생 시 응답이 커밋되지 않았을 때만 상태 코드 설정
                if (!response.isCommitted()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
                // log.error("SSE 요청 처리 중 오류 발생: {}", e.getMessage()); // Original code had this
                // line commented out
            }
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
    // 또는 쿠키에서 토큰 추출 (SSE의 경우)
    private String getToken(HttpServletRequest request) {
        // 헤더에서 토큰 추출 (일반 API 요청)
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        String requestURI = request.getRequestURI();
        boolean isNotification = Arrays.stream(AuthPath.NOTIFICATION_URLS)
                .anyMatch(requestURI::startsWith);

        // SSE 요청의 경우 쿠키에서 SSE 토큰 추출
        if (isNotification) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("sseToken".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }

        return null;
    }

}
