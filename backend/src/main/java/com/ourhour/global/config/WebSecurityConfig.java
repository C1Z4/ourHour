package com.ourhour.global.config;

import com.ourhour.global.constant.AuthPath;
import com.ourhour.global.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars").permitAll()
                        // 비인증 요청 허용
                        .requestMatchers(AuthPath.PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}