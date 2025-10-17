package com.ourhour.domain.notification.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 알림 메시지 템플릿
 * 각 알림 타입별 제목과 메시지 형식을 정의합니다.
 */
@Getter
@Setter
public class NotificationMessageTemplate {

    private String titleTemplate;
    private String messageTemplate;

    public NotificationMessageTemplate(String titleTemplate, String messageTemplate) {
        this.titleTemplate = titleTemplate;
        this.messageTemplate = messageTemplate;
    }

    /**
     * 템플릿에 파라미터를 적용하여 실제 메시지 생성
     */
    public String formatTitle(Object... args) {
        return String.format(titleTemplate, args);
    }

    /**
     * 템플릿에 파라미터를 적용하여 실제 메시지 생성
     */
    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
