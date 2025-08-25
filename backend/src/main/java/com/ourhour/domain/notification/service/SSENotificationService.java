package com.ourhour.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.dto.SSEEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSENotificationService {

    private final ObjectMapper objectMapper;

    // 사용자별 SSE 연결을 관리하는 맵
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결 타임아웃
    private static final long SSE_TIMEOUT = 60 * 1000L;

    // 사용자별 SSE 연결 생성
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // 기존 연결이 있다면 종료
        SseEmitter existingEmitter = emitters.get(userId);
        if (existingEmitter != null) {
            existingEmitter.complete();
        }

        emitters.put(userId, emitter);

        // 연결 완료 및 오류 처리
        emitter.onCompletion(() -> {
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
        });

        emitter.onError((ex) -> {
            emitters.remove(userId);
        });

        // 연결 성공 이벤트 전송
        try {
            SSEEventDTO connectEvent = SSEEventDTO.builder()
                    .type("connected")
                    .data(Map.of("message", "알림 서비스에 연결되었습니다"))
                    .build();

            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(objectMapper.writeValueAsString(connectEvent)));

            log.info("✅ [SSE] 연결 성공: userId={}", userId);

            // 연결 유지를 위한 heartbeat 스케줄러 시작
            startHeartbeat(userId, emitter);

        } catch (IOException e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
            log.error("❌ [SSE] 초기 이벤트 전송 실패: userId={}", userId, e);
        }

        return emitter;
    }

    // 알림 전송
    public void sendNotification(Long userId, NotificationDTO notification) {
        SseEmitter emitter = emitters.get(userId);

        log.info("🔔 [SSE] 알림 전송 시도: userId={}, notificationId={}, 연결상태={}",
                userId, notification.getNotificationId(), emitter != null ? "연결됨" : "연결안됨");

        if (emitter != null) {
            try {
                SSEEventDTO event = SSEEventDTO.builder()
                        .type("notification")
                        .data(notification)
                        .build();

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(objectMapper.writeValueAsString(event)));

                log.info("✅ [SSE] 알림 전송 성공: userId={}, notificationId={}", userId, notification.getNotificationId());

            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
                log.error("❌ [SSE] 알림 전송 실패: userId={}, notificationId={}", userId, notification.getNotificationId(),
                        e);
            }
        } else {
            log.warn("⚠️ [SSE] SSE 연결 없음 - 알림 전송 스킵: userId={}, notificationId={}, 현재 연결된 사용자들={}",
                    userId, notification.getNotificationId(), emitters.keySet());
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
                log.error("읽음 처리 알림 전송 실패: userId={}, notificationId={}", userId, notificationId, e);
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
                log.error("전체 읽음 처리 알림 전송 실패: userId={}", userId, e);
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

    // 연결 유지를 위한 heartbeat 메시지 전송
    private void startHeartbeat(Long userId, SseEmitter emitter) {
        // 30초마다 heartbeat 메시지 전송
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (emitters.containsKey(userId) && emitter != null) {
                        emitter.send(SseEmitter.event()
                                .name("ping")
                                .data("ping"));

                    } else {
                        timer.cancel();
                        emitters.remove(userId);
                    }
                } catch (IOException e) {
                    timer.cancel();
                    emitters.remove(userId);
                } catch (Exception e) {
                    timer.cancel();
                    emitters.remove(userId);
                }
            }
        }, 30000, 30000); // 30초 후 시작, 30초마다 반복
    }
}