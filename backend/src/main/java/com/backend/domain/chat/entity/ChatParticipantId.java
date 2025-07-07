package com.backend.domain.chat.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class ChatParticipantId implements Serializable {

    private Long roomId;
    private Long memberId;
}
