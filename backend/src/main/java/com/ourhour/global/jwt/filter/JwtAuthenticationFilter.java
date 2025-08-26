package com.ourhour.global.jwt.filter;

import com.ourhour.global.constant.AuthPath;
import com.ourhour.global.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        // 토큰 추출 (헤더 또는 쿠키)
        String token = getToken(request);

        // SSE 요청인지 확인
        boolean isNotification = Arrays.stream(AuthPath.NOTIFICATION_URLS)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        if (isNotification) {
            // SSE 요청의 경우 특별 처리
            try {
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    // 정상 토큰이면 SecurityContext 등록
                    Authentication authentication = jwtTokenProvider.getAuthenticationFromToken(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 토큰이 유효하지 않으면 인증 실패 처리
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return; // 필터 체인 중단
                }
            } catch (Exception e) {
                // SSE 요청에서 예외 발생 시 응답이 커밋되지 않았을 때만 상태 코드 설정
                if (!response.isCommitted()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
                return; // 필터 체인 중단
            }
        } else {
            // 일반 API 요청의 경우, 유효한 토큰이 있으면 SecurityContext에 저장
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthenticationFromToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 URL 목록
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

    // 헤더 또는 쿠키에서 토큰 추출 (SSE의 경우)
    private String getToken(HttpServletRequest request) {
        // 헤더에서 토큰 추출 (일반 API 요청)
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        String requestURI = request.getRequestURI();
        boolean isNotification = Arrays.stream(AuthPath.NOTIFICATION_URLS)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        // SSE 요청의 경우 쿠키에서 SSE 토큰 추출
        if (isNotification) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("sseToken".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }

        return null;
    }
}