package com.ourhour.domain.notification.dto;

import com.ourhour.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCreateReqDTO {
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private Long relatedId;
    private String relatedType;
    private String actionUrl;
}