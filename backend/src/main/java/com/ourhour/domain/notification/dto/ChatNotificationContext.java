package com.ourhour.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatNotificationContext {
    private Long userId;
    private String senderName;
    private String roomName;
    private Long roomId;
    private Long orgId;
}
