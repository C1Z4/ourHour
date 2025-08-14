package com.ourhour.global.config;

import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

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

            // preSend: 보내기 직전 검사
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 메시지가 전송될 때 마다 검증하지 않고 처음 연결을 시도할 경우에만 검증
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

                        // 인증되면 Authentication 객체 붙여줌
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims, null, null);
                        accessor.setUser(authentication);

                        // @MessageMapping이 아닌 곳에서의 인증정보 사용 or 비동기처리
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);
                    }
                }
                return message;
            }
        });
    }
}
