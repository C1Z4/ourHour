package com.ourhour.global.config;

import com.ourhour.global.constant.AuthPath;
import com.ourhour.global.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // SecurityContext를 모든 스레드에서 공유하도록 설정 (SSE, 비동기 처리용)
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        http
                // JWT 기반 -> CSRF 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // WebConfig에 있는 CORS 설정을 Spring Security에 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // WebSocket STOMP 연결 테스트를 위한 구문
                        .requestMatchers("/ws-stomp/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Swagger/OpenAPI 문서 경로 허용
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars")
                        .permitAll()
                        // SSE 알림 스트림 엔드포인트 허용 (JWT 필터에서 자체 인증 처리)
                        .requestMatchers(AuthPath.NOTIFICATION_URLS).permitAll()
                        // 비인증 요청 허용
                        .requestMatchers(AuthPath.PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                // 예외 처리 설정
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            // SSE 요청인 경우 특별 처리
                            String requestURI = request.getRequestURI();
                            boolean isSSERequest = requestURI.contains("/notifications/stream");

                            if (isSSERequest && !response.isCommitted()) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("text/plain; charset=UTF-8");
                                response.getWriter().write("SSE Authentication failed: " + authException.getMessage());
                                response.flushBuffer();
                            } else if (!isSSERequest && !response.isCommitted()) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json; charset=UTF-8");
                                response.getWriter().write("{\"error\":\"Authentication required\"}");
                                response.flushBuffer();
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // SSE 요청인 경우 특별 처리
                            String requestURI = request.getRequestURI();
                            boolean isSSERequest = requestURI.contains("/notifications/stream");

                            if (isSSERequest && !response.isCommitted()) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("text/plain; charset=UTF-8");
                                response.getWriter().write("SSE Access denied: " + accessDeniedException.getMessage());
                                response.flushBuffer();
                            } else if (!isSSERequest && !response.isCommitted()) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json; charset=UTF-8");
                                response.getWriter().write("{\"error\":\"Access denied\"}");
                                response.flushBuffer();
                            }
                        }))
                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}