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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/signup") || requestURI.startsWith("/api/auth/signin") || requestURI.startsWith("/api/auth/email-verification")) {
            filterChain.doFilter(request, response); // signup과 signin에 대해서는 인증 필터를 거칠 필요가 없음.

            return;
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
