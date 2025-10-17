package com.ourhour.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.dto.SSEEventDTO;
import com.ourhour.domain.notification.model.SseConnection;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSENotificationService {

    private final ObjectMapper objectMapper;

    // 설정값
    @Value("${notification.sse.timeout:1800000}")
    private long sseTimeout;

    @Value("${notification.sse.heartbeat-pool-size:10}")
    private int heartbeatPoolSize;

    @Value("${notification.sse.heartbeat-interval:15}")
    private int heartbeatInterval;

    @Value("${notification.sse.cleanup-interval:60}")
    private int cleanupIntervalSeconds;

    @Value("${notification.sse.connection-timeout:30}")
    private long connectionTimeoutMinutes;

    // 사용자별 SSE 연결을 관리하는 맵 (SseConnection으로 변경)
    private final Map<Long, SseConnection> connections = new ConcurrentHashMap<>();

    // heartbeat용 스케줄러
    private ScheduledExecutorService heartbeatScheduler;

    @PostConstruct
    public void init() {
        this.heartbeatScheduler = Executors.newScheduledThreadPool(heartbeatPoolSize);
    }

    // 사용자별 SSE 연결 생성
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(sseTimeout);

        // 기존 연결이 있다면 정리
        cleanupConnection(userId);

        // SseConnection 객체 생성
        SseConnection connection = new SseConnection(userId, emitter);
        connections.put(userId, connection);

        // 연결 완료 및 오류 처리
        emitter.onCompletion(() -> {
            cleanupConnection(userId);
        });

        emitter.onTimeout(() -> {
            cleanupConnection(userId);
        });

        emitter.onError((ex) -> {
            cleanupConnection(userId);
        });

        // 연결 유지를 위한 heartbeat 스케줄러 시작 (초기 메시지 포함)
        startHeartbeat(connection);

        return emitter;
    }

    // SSE 이벤트 전송 공통 메소드
    private void sendEvent(Long userId, String eventName, String eventType, Object data) {
        SseConnection connection = connections.get(userId);

        if (connection != null && connection.isValid()) {
            try {
                SSEEventDTO event = SSEEventDTO.builder()
                        .type(eventType)
                        .data(data)
                        .build();

                connection.getEmitter().send(SseEmitter.event()
                        .name(eventName)
                        .data(objectMapper.writeValueAsString(event)));

                // 마지막 활동 시간 업데이트
                connection.updateLastActivity();

            } catch (IOException e) {
                cleanupConnection(userId);
            }
        }
    }

    // 알림 전송 (SecurityContext 없이도 작동하도록 userId 명시적 전달)
    public void sendNotification(Long userId, NotificationDTO notification) {
        sendEvent(userId, "notification", "notification", notification);
    }

    // 알림 읽음 처리
    public void sendNotificationRead(Long userId, Long notificationId) {
        sendEvent(userId, "notification", "notification_read",
                Map.of("notificationId", notificationId));
    }

    // 모든 알림 읽음 처리
    public void sendAllNotificationsRead(Long userId) {
        sendEvent(userId, "notification", "all_notifications_read",
                Map.of("message", "모든 알림이 읽음 처리되었습니다"));
    }

    // 사용자별 SSE 연결 상태 확인
    public boolean isConnected(Long userId) {
        SseConnection connection = connections.get(userId);
        return connection != null && connection.isValid();
    }

    // 현재 활성 연결 개수 조회
    public int getActiveConnectionCount() {
        return connections.size();
    }

    // 연결 정리 (SseConnection을 통한 통합 정리)
    private void cleanupConnection(Long userId) {
        SseConnection connection = connections.remove(userId);
        if (connection != null) {
            try {
                connection.cleanup();
                log.debug("Cleaned up SSE connection for user {}", userId);
            } catch (Exception e) {
                log.debug("Failed to cleanup connection for user {}: {}", userId, e.getMessage());
            }
        }
    }

    // SecurityContext 내에서 작업 실행하는 헬퍼 메서드
    private void executeWithSecurityContext(SecurityContext context, Runnable task) {
        SecurityContextHolder.setContext(context);
        try {
            task.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    // 연결 유지를 위한 heartbeat 메시지 전송 (ScheduledExecutorService 사용)
    private void startHeartbeat(SseConnection connection) {
        Long userId = connection.getUserId();
        SseEmitter emitter = connection.getEmitter();

        // 현재 SecurityContext 캡처 (HTTP 요청 스레드에서)
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // 초기 연결 메시지를 즉시 전송 (연결 안정화)
        try {
            if (connections.containsKey(userId) && emitter != null) {
                emitter.send(SseEmitter.event()
                        .name("connection")
                        .data("connected"));
                connection.updateLastActivity();
            }
        } catch (Exception e) {
            log.debug("Failed to send initial connection message for user {}: {}", userId, e.getMessage());
            cleanupConnection(userId);
            return; // 초기 연결 실패 시 heartbeat 시작하지 않음
        }

        // 15초마다 heartbeat 메시지 전송 (연결 끊김 즉시 감지)
        ScheduledFuture<?> heartbeatTask = heartbeatScheduler.scheduleAtFixedRate(() -> {
            executeWithSecurityContext(securityContext, () -> {
                try {
                    SseConnection currentConnection = connections.get(userId);
                    if (currentConnection != null && currentConnection.isValid()) {
                        // Emitter 상태 체크
                        try {
                            currentConnection.getEmitter().send(SseEmitter.event()
                                    .name("ping")
                                    .data("ping"));
                            currentConnection.updateLastActivity();

                        } catch (IllegalStateException e) {
                            // ResponseBodyEmitter has already completed
                            cleanupConnection(userId);
                        }
                    } else {
                        cleanupConnection(userId);
                    }
                } catch (IOException e) {
                    cleanupConnection(userId);
                } catch (Exception e) {
                    cleanupConnection(userId);
                }
            });
        }, heartbeatInterval, heartbeatInterval, TimeUnit.SECONDS);

        // heartbeat 작업 저장
        connection.setHeartbeatTask(heartbeatTask);
    }

    // 만료된 연결을 주기적으로 정리하는 스케줄러
    @Scheduled(fixedDelayString = "${notification.sse.cleanup-interval:60}000")
    public void cleanupStaleConnections() {
        List<Long> staleUserIds = new ArrayList<>();

        connections.forEach((userId, connection) -> {
            if (connection.isExpired(connectionTimeoutMinutes)) {
                staleUserIds.add(userId);
                log.info("Detected stale SSE connection for user {}. Last activity: {}",
                        userId, connection.getLastActivityAt());
            }
        });

        staleUserIds.forEach(this::cleanupConnection);

        if (!staleUserIds.isEmpty()) {
            log.info("Cleaned up {} stale SSE connections. Current active connections: {}",
                    staleUserIds.size(), connections.size());
        }
    }

    // 서비스 종료 시 모든 연결 정리
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SSE notification service. Active connections: {}", connections.size());

        // 모든 연결 정리
        connections.forEach((userId, connection) -> {
            try {
                connection.cleanup();
            } catch (Exception e) {
                log.debug("Failed to cleanup connection for user {} during shutdown: {}", userId, e.getMessage());
            }
        });
        connections.clear();

        // 스케줄러 종료
        heartbeatScheduler.shutdown();
        try {
            if (!heartbeatScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Heartbeat scheduler did not terminate within 5 seconds, forcing shutdown");
                heartbeatScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for heartbeat scheduler to terminate", e);
            heartbeatScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("SSE notification service shutdown completed");
    }
}