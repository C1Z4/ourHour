package com.ourhour.domain.notification.controller;

import com.ourhour.domain.notification.dto.NotificationPageResDTO;
import com.ourhour.domain.notification.service.NotificationService;
import com.ourhour.domain.notification.service.SSENotificationService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.util.SecurityUtil;
import com.ourhour.domain.user.exception.UserException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림", description = "실시간 알림 관리 API")
public class NotificationController {

    private final NotificationService notificationService;
    private final SSENotificationService sseNotificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE 알림 스트림", description = "실시간 알림을 위한 SSE 연결을 생성합니다.")
    public SseEmitter streamNotifications(HttpServletRequest request, HttpServletResponse response) {
        log.info("🔌 [SSE] 연결 시도: IP={}, Origin={}, User-Agent={}", 
                request.getRemoteAddr(), 
                request.getHeader("Origin"), 
                request.getHeader("User-Agent"));
                
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("🔌 [SSE] 인증 확인: userId={}", userId);

        if (userId == null) {
            log.error("❌ [SSE] 인증 실패: SecurityContext에서 userId 없음");
            throw UserException.userNotFoundException();
        }

        // SSE 응답 헤더 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Content-Type", "text/event-stream; charset=UTF-8");
        response.setHeader("X-Accel-Buffering", "no"); // Nginx 버퍼링 방지
        
        // CORS 헤더는 Spring Security CORS 설정에 의존

        log.info("🔌 [SSE] SSE 연결 생성 중: userId={}", userId);
        SseEmitter emitter = sseNotificationService.subscribe(userId);
        log.info("🔌 [SSE] SSE 연결 생성 완료: userId={}", userId);
        
        return emitter;
    }

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<ApiResponse<NotificationPageResDTO>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUserId();

        NotificationPageResDTO response = notificationService.getNotifications(userId, page, size);

        return ResponseEntity.ok(ApiResponse.success(response, "알림 목록 조회에 성공했습니다."));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "읽지 않은 알림 개수", description = "사용자의 읽지 않은 알림 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();

        long unreadCount = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(ApiResponse.success(unreadCount, "읽지 않은 알림 개수 조회에 성공했습니다."));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        Long userId = SecurityUtil.getCurrentUserId();

        notificationService.markAsRead(notificationId, userId);

        // SSE로 읽음 처리 이벤트 전송
        sseNotificationService.sendNotificationRead(userId, notificationId);

        return ResponseEntity.ok(ApiResponse.success(null, "알림 읽음 처리에 성공했습니다."));
    }

    @PutMapping("/read-all")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();

        int updatedCount = notificationService.markAllAsRead(userId);

        // SSE로 전체 읽음 처리 이벤트 전송
        sseNotificationService.sendAllNotificationsRead(userId);

        return ResponseEntity.ok(ApiResponse.success(updatedCount, "모든 알림 읽음 처리에 성공했습니다."));
    }

    @GetMapping("/connection-status")
    @Operation(summary = "SSE 연결 상태", description = "현재 SSE 연결 상태를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> getConnectionStatus() {
        Long userId = SecurityUtil.getCurrentUserId();

        boolean isConnected = sseNotificationService.isConnected(userId);

        return ResponseEntity.ok(ApiResponse.success(isConnected, "SSE 연결 상태 조회에 성공했습니다."));
    }
}