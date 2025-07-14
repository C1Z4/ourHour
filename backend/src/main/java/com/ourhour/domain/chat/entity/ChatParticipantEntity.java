package com.ourhour.domain.chat.entity;

import com.ourhour.domain.member.entity.MemberEntity;
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
    private ChatParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoomEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    // 정적 팩토리 메소드
    public static ChatParticipantEntity createParticipant(ChatRoomEntity chatRoom, MemberEntity member) {
        ChatParticipantEntity participant = new ChatParticipantEntity();
        participant.chatRoomEntity = chatRoom;
        participant.memberEntity = member;
        participant.id = new ChatParticipantId(chatRoom.getRoomId(), member.getMemberId());
        return participant;
    }
}