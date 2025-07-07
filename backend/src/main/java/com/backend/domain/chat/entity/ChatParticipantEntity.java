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
@IdClass(ChatParticipantId.class)
public class ChatParticipantEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoom;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;
}
