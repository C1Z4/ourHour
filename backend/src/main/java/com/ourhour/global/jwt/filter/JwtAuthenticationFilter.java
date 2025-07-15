package com.ourhour.global.jwt.filter;

import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final Set<String> EXCLUDE_URI_PREFIXES = Set.of(
            "/api/auth/signup",
            "/api/auth/signin",
            "/api/auth/email-verification",
            "/api/auth/token"
    );

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        boolean skipFilter = EXCLUDE_URI_PREFIXES.stream().anyMatch(requestURI::startsWith);

        if (skipFilter) {
            filterChain.doFilter(request, response);

            return;
        }

        // 민감 정보 응답에 대한 캐시 방지 헤더 추가
        if (requestURI.startsWith("/api/auth") || requestURI.startsWith("/api/user")) {
            response.setHeader("Cache-Control", "no-store");
        }

        try {
            String token = getToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Claims claims = jwtTokenProvider.parseAccessToken(token);
                UserContextHolder.set(claims);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                return;
            }

            // 필터 체인의 다음 필터로 전달. 필터가 없다면 서블릿으로 전달
            filterChain.doFilter(request, response);
        } finally {
            // 메모리 누수 방지
            UserContextHolder.clear();
        }

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
