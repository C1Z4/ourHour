package com.ourhour.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatParticipantResDTO {

    private Long memberId;
    private String memberName;
    private String profileImageUrl;
}
