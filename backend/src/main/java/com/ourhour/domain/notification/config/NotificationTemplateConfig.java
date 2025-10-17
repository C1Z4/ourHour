package com.ourhour.domain.notification.config;

import com.ourhour.domain.notification.enums.NotificationType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * 알림 메시지 템플릿 설정
 * 각 알림 타입별로 제목과 메시지 형식을 중앙에서 관리합니다.
 */
@Configuration
public class NotificationTemplateConfig {

    @Bean
    public Map<NotificationType, NotificationMessageTemplate> notificationTemplates() {
        Map<NotificationType, NotificationMessageTemplate> templates = new EnumMap<>(NotificationType.class);

        // 채팅 메시지 알림
        templates.put(NotificationType.CHAT_MESSAGE,
                new NotificationMessageTemplate(
                        "%s님의 메시지",
                        "'%s' 채팅방에 새 메시지가 있습니다."));

        // 이슈 할당 알림
        templates.put(NotificationType.ISSUE_ASSIGNED,
                new NotificationMessageTemplate(
                        "이슈 할당",
                        "'%s' 이슈가 할당되었습니다."));

        // 이슈 댓글 알림
        templates.put(NotificationType.ISSUE_COMMENT,
                new NotificationMessageTemplate(
                        "이슈 댓글",
                        "%s님이 '%s' 이슈에 댓글을 남겼습니다."));

        // 이슈 댓글 답글 알림
        templates.put(NotificationType.ISSUE_COMMENT_REPLY,
                new NotificationMessageTemplate(
                        "이슈 댓글 답글",
                        "%s님이 '%s' 이슈에 댓글에 답글을 남겼습니다."));

        // 게시글 댓글 알림
        templates.put(NotificationType.POST_COMMENT,
                new NotificationMessageTemplate(
                        "게시글 댓글",
                        "%s님이 '%s' 게시글에 댓글을 남겼습니다."));

        // 게시글 댓글 답글 알림
        templates.put(NotificationType.POST_COMMENT_REPLY,
                new NotificationMessageTemplate(
                        "댓글 답글",
                        "%s님이 '%s' 게시글의 댓글에 답글을 남겼습니다."));

        // 댓글 답글 알림
        templates.put(NotificationType.COMMENT_REPLY,
                new NotificationMessageTemplate(
                        "댓글 답글",
                        "%s님이 회원님의 댓글에 답글을 남겼습니다."));

        return templates;
    }
}
