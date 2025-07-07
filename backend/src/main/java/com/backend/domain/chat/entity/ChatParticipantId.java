package com.backend.domain.chat.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class ChatParticipantId implements Serializable {

    private Long chatRoom;
    private Long member;
}
