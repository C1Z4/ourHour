package com.ourhour.domain.notification.model;

import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

/**
 * SSE 연결 정보를 관리하는 wrapper 클래스
 * Emitter와 관련된 메타데이터를 함께 관리하여 메모리 누수를 방지합니다.
 */
@Getter
public class SseConnection {

    private final Long userId;
    private final SseEmitter emitter;
    private final LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
    private ScheduledFuture<?> heartbeatTask;

    public SseConnection(Long userId, SseEmitter emitter) {
        this.userId = userId;
        this.emitter = emitter;
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * Heartbeat 작업 설정
     */
    public void setHeartbeatTask(ScheduledFuture<?> heartbeatTask) {
        this.heartbeatTask = heartbeatTask;
    }

    /**
     * 마지막 활동 시간 업데이트
     */
    public void updateLastActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * 연결이 만료되었는지 확인 (기본 30분)
     */
    public boolean isExpired(long timeoutMinutes) {
        return LocalDateTime.now().isAfter(lastActivityAt.plusMinutes(timeoutMinutes));
    }

    /**
     * 연결 정리
     */
    public void cleanup() {
        // Heartbeat 작업 취소
        if (heartbeatTask != null && !heartbeatTask.isDone()) {
            heartbeatTask.cancel(true);
        }

        // Emitter 완료 처리
        try {
            emitter.complete();
        } catch (Exception e) {
            // 이미 완료된 emitter는 무시
        }
    }

    /**
     * 연결이 유효한지 확인
     */
    public boolean isValid() {
        return emitter != null &&
               (heartbeatTask == null || !heartbeatTask.isCancelled());
    }
}
