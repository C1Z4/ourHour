package com.ourhour.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomListResDTO {

    private Long roomId;
    private String name;
    private String lastMessage;
    private LocalDateTime lastMessageTimestamp;
}

