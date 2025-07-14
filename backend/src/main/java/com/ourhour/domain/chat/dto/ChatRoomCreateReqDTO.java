package com.ourhour.domain.chat.dto;

import com.ourhour.global.common.enums.TagColor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChatRoomCreateReqDTO {

    private String name;
    private TagColor color;
    private List<Long> memberIds;
}