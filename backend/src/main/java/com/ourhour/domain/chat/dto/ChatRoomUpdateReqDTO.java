package com.ourhour.domain.chat.dto;

import com.ourhour.global.common.enums.TagColor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomUpdateReqDTO {

    private String name;
    private TagColor color;
}