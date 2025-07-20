package com.ourhour.domain.chat.dto;

import com.ourhour.global.common.enums.TagColor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomListResDTO {

    private Long roomId;
    private String name;
    private TagColor color;
    private String lastMessage;
    private LocalDateTime lastMessageTimestamp;
}

