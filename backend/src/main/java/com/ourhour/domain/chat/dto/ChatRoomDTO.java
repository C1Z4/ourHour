package com.ourhour.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ourhour.global.common.enums.TagColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long roomId;
    private String name;
    private TagColor color;
    private String lastMessage;
    private LocalDateTime lastMessageTimestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    private List<ChatParticipantDTO> chatParticipants;


}
