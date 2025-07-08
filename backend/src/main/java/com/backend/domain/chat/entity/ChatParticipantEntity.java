package com.backend.domain.chat.entity;

import com.backend.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_chat_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipantEntity {

    @EmbeddedId
    private ChatParticipantId chatParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoomEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;
}