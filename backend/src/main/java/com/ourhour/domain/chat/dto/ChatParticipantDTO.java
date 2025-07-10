package com.ourhour.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantDTO {

    private Long roomId;
    private Long memberId;
    private String memberName;
    private String profileImgUrl;
}
