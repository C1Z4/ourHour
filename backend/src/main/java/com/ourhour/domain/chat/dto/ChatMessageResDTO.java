package com.ourhour.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResDTO {

    private Long chatMessageId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime timestamp;
}