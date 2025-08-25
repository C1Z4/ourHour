package com.ourhour.global.jwt.filter;

import com.ourhour.global.constant.AuthPath;
import com.ourhour.global.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
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
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 민감 정보 응답에 대한 캐시 방지 헤더 추가
        if (requestURI.startsWith("/api/auth") || requestURI.startsWith("/api/user")) {
            response.setHeader("Cache-Control", "no-store");
        }

        // HttpRequest -> token 추출
        String token = getToken(request);

        // 토큰이 존재하고 유효한 경우에만 SecurityContext에 인증 정보 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // token -> Authentication 객체 파싱
            Authentication authentication = jwtTokenProvider.getAuthenticationFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        String[] permitAllUrls = concatArrays(
                AuthPath.PUBLIC_URLS,
                AuthPath.SWAGGER_URLS,
                AuthPath.STOMP_URLS,
                AuthPath.MONITORING_URLS
        );

        // 현재 요청 URI가 허용 목록에 있는지 확인
        return Arrays.stream(permitAllUrls)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    // 여러 문자열 배열을 하나로 합치는 헬퍼 메서드
    private String[] concatArrays(String[]... arrays) {
        return Arrays.stream(arrays)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
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
