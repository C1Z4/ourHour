package com.ourhour.global.config;

import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    // 세션에서 인증 정보를 저장하고 조회하기 위한 키
    private static final String AUTH_KEY = "userAuthentication";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    // WebSocket 인증을 위한 게이트웨이, 클라이언트 -> 서버로 들어오는 채널
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 클라이언트가 WebSocket을 통해 보내는 모든 메시지가 인터셉터를 통과해야함
        registration.interceptors(new ChannelInterceptor() {

            // preSend: 메시지를 처리하기 직전에 호출
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 처음 연결을 시도할 경우(CONNECT)에만 인증 처리
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 헤더에서 JWT 추출
                    String jwtToken = accessor.getFirstNativeHeader("Authorization");
                    if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                        jwtToken = jwtToken.substring(7);
                    }

                    // JWT validate 검사
                    if (jwtTokenProvider.validateToken(jwtToken)) {
                        // JWT에서 Claim 추출
                        Claims claims = jwtTokenProvider.parseAccessToken(jwtToken);

                        // 인증되면 Authentication 객체를 생성
                        Authentication authentication = new UsernamePasswordAuthenticationToken(claims, null, null);

                        // WebSocket 세션 속성에 인증 객체를 직접 저장 (가장 안정적인 방법)
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            sessionAttributes.put(AUTH_KEY, authentication);
                        }
                    }
                }
                // 실제 메시지를 보낼 경우(SEND), SecurityContext에 인증 정보 설정
                else if (StompCommand.SEND.equals(accessor.getCommand())) {
                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    if (sessionAttributes != null) {
                        // 세션에서 저장해둔 인증 정보를 꺼내 SecurityContext에 설정
                        Authentication authentication = (Authentication) sessionAttributes.get(AUTH_KEY);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                return message;
            }

            // postSend: 메시지 처리가 완료된 후 호출
            @Override
            public void postSend(@NonNull Message<?> message, @NonNull MessageChannel channel, boolean sent) {
                // 현재 스레드의 SecurityContext를 정리하여 다른 요청에 영향이 없도록 함
                SecurityContextHolder.clearContext();
            }
        });
    }
}