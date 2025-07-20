package com.ourhour.domain.chat.dto;

import com.ourhour.global.common.enums.TagColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomDetailResDTO {

    private Long roomId;
    private String name;
    private TagColor color;
    private LocalDateTime createdAt;
    private Long orgId;
}
