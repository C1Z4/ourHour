package com.ourhour.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageResDTO {

    private Long chatRoomId;
    private Long chatMessageId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime timestamp;
}