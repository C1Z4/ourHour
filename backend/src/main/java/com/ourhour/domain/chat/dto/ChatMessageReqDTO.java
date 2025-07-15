package com.ourhour.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageReqDTO {

    private Long chatRoomId;
    private Long senderId;
    private String message;
}