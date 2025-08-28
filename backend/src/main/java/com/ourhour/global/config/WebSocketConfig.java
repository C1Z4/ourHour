package com.ourhour.global.config;

import com.ourhour.global.jwt.JwtTokenProvider;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String USER_AUTHENTICATION_KEY = "userAuthentication";

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

                if (accessor == null) {
                    System.out.println("StompHeaderAccessor is null, cannot process message.");
                    return message;
                }

                // CONNECT: 최초 연결 시, 인증 정보를 세션에 저장
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String jwtToken = accessor.getFirstNativeHeader("Authorization");
                    if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                        jwtToken = jwtToken.substring(7);
                    }

                    if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
                        Authentication authentication = jwtTokenProvider.getAuthenticationFromToken(jwtToken);

                        Objects.requireNonNull(accessor.getSessionAttributes()).put(USER_AUTHENTICATION_KEY, authentication);
                        accessor.setUser(authentication); // accessor 자체에도 유저 정보를 설정
                        System.out.println("WebSocket CONNECT successful, Authentication stored in session for user: " + authentication.getName());
                    } else {
                        System.out.println("============================================");
                        System.out.println("WebSocket CONNECT failed: Invalid JWT Token.");
                        System.out.println("============================================");
                    }
                }

                // SEND, SUBSCRIBE 등 다른 모든 메시지 처리 시
                else if (StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    Authentication authentication = (Authentication) Objects.requireNonNull(accessor.getSessionAttributes()).get(USER_AUTHENTICATION_KEY);
                    if (authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Authentication restored from session for user: " + authentication.getName());
                    } else {
                        System.out.println("===============================================================");
                        System.out.println("Authentication not found in session for SEND/SUBSCRIBE command.");
                        System.out.println("===============================================================");
                    }
                }

                return message;
            }
        });
    }
}
