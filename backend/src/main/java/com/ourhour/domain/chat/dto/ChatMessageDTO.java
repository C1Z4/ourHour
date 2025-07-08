package com.ourhour.domain.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private Long chatRoomId;
    private Long chatMessageId;
    private Long senderId;
    private String message;
    private LocalDateTime timestamp;
}
