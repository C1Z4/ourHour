package com.ourhour.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.dto.SSEEventDTO;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class SSENotificationService {

    private final ObjectMapper objectMapper;

    // 사용자별 SSE 연결을 관리하는 맵
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 사용자별 heartbeat 작업을 관리하는 맵
    private final Map<Long, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();

    // heartbeat용 스케줄러
    private final ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(10);

    // SSE 연결 타임아웃 (30분)
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    // 사용자별 SSE 연결 생성
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

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

    // 알림 전송 (SecurityContext 없이도 작동하도록 userId 명시적 전달)
    public void sendNotification(Long userId, NotificationDTO notification) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                SSEEventDTO event = SSEEventDTO.builder()
                        .type("notification")
                        .data(notification)
                        .build();

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(objectMapper.writeValueAsString(event)));

            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }

    // 알림 읽음 처리
    public void sendNotificationRead(Long userId, Long notificationId) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                SSEEventDTO event = SSEEventDTO.builder()
                        .type("notification_read")
                        .data(Map.of("notificationId", notificationId))
                        .build();

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(objectMapper.writeValueAsString(event)));

            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }

    // 모든 알림 읽음 처리
    public void sendAllNotificationsRead(Long userId) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                SSEEventDTO event = SSEEventDTO.builder()
                        .type("all_notifications_read")
                        .data(Map.of("message", "모든 알림이 읽음 처리되었습니다"))
                        .build();

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(objectMapper.writeValueAsString(event)));

            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
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
            }
        }

        // 기존 heartbeat 작업 정리
        ScheduledFuture<?> existingTask = heartbeatTasks.remove(userId);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(true);
        }
    }

    // 연결 유지를 위한 heartbeat 메시지 전송 (ScheduledExecutorService 사용)
    private void startHeartbeat(Long userId, SseEmitter emitter) {
        // 현재 SecurityContext 캡처 (HTTP 요청 스레드에서)
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // 초기 연결 메시지를 3초 후에 전송 (청크 인코딩 안정화)
        ScheduledFuture<?> initialTask = heartbeatScheduler.schedule(() -> {
            SecurityContextHolder.setContext(securityContext);
            try {
                if (emitters.containsKey(userId) && emitter != null) {
                    emitter.send(SseEmitter.event()
                            .name("connection")
                            .data("connected"));
                }
            } catch (Exception e) {
            } finally {
                SecurityContextHolder.clearContext();
            }
        }, 3, TimeUnit.SECONDS);

        // 30초마다 heartbeat 메시지 전송
        ScheduledFuture<?> heartbeatTask = heartbeatScheduler.scheduleAtFixedRate(() -> {
            // SecurityContext를 스케줄러 스레드에 설정
            SecurityContextHolder.setContext(securityContext);

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
            } finally {
                // SecurityContext 정리
                SecurityContextHolder.clearContext();
            }
        }, 30, 30, TimeUnit.SECONDS); // 30초 후 시작, 30초마다 반복

        // heartbeat 작업 저장
        heartbeatTasks.put(userId, heartbeatTask);
    }

    // 서비스 종료 시 모든 연결 정리
    @PreDestroy
    public void shutdown() {
        // 모든 emitter 정리
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.complete();
            } catch (Exception e) {
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
                heartbeatScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            heartbeatScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}