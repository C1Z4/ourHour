package com.ourhour.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.dto.SSEEventDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
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

    // 사용자별 SSE 연결을 관리하는 맵
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 사용자별 heartbeat 작업을 관리하는 맵
    private final Map<Long, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();

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

        emitters.put(userId, emitter);

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
        startHeartbeat(userId, emitter);

        return emitter;
    }

    // SSE 이벤트 전송 공통 메소드
    private void sendEvent(Long userId, String eventName, String eventType, Object data) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                SSEEventDTO event = SSEEventDTO.builder()
                        .type(eventType)
                        .data(data)
                        .build();

                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(objectMapper.writeValueAsString(event)));

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
        return emitters.containsKey(userId);
    }

    // 현재 활성 연결 개수 조회
    public int getActiveConnectionCount() {
        return emitters.size();
    }

    // 연결 정리 (emitter와 heartbeat 작업 모두 정리)
    private void cleanupConnection(Long userId) {
        // 기존 emitter 정리
        SseEmitter existingEmitter = emitters.remove(userId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.debug("Failed to complete emitter for user {}: {}", userId, e.getMessage());
            }
        }

        // 기존 heartbeat 작업 정리
        ScheduledFuture<?> existingTask = heartbeatTasks.remove(userId);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(true);
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
    private void startHeartbeat(Long userId, SseEmitter emitter) {
        // 현재 SecurityContext 캡처 (HTTP 요청 스레드에서)
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // 초기 연결 메시지를 즉시 전송 (연결 안정화)
        try {
            if (emitters.containsKey(userId) && emitter != null) {
                emitter.send(SseEmitter.event()
                        .name("connection")
                        .data("connected"));
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
                    if (emitters.containsKey(userId) && emitter != null) {
                        // Emitter 상태 체크
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("ping")
                                    .data("ping"));

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
        heartbeatTasks.put(userId, heartbeatTask);
    }

    // 서비스 종료 시 모든 연결 정리
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SSE notification service. Active connections: {}", emitters.size());

        // 모든 emitter 정리
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("Failed to complete emitter for user {} during shutdown: {}", userId, e.getMessage());
            }
        });
        emitters.clear();

        // 모든 heartbeat 작업 취소
        heartbeatTasks.forEach((userId, task) -> {
            if (task != null && !task.isDone()) {
                task.cancel(true);
            }
        });
        heartbeatTasks.clear();

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