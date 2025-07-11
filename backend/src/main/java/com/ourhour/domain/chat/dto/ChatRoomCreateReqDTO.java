package com.ourhour.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatRoomCreateReqDTO {

    private String name;
    private List<Long> memberIds;
}