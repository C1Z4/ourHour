package com.ourhour.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ourhour.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationDTO {
    private Long notificationId;
    private NotificationType type;
    private String title;
    private String message;

    @JsonProperty("isRead")
    private boolean isRead;

    private LocalDateTime createdAt;
    private Long relatedId;
    private String relatedType;
    private String actionUrl;
}