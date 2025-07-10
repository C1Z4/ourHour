package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, ChatParticipantId> {

    List<ChatParticipantEntity> findAllByChatRoom_RoomId(Long roomId);

    Optional<ChatParticipantEntity> findByChatRoom_RoomIdAndMember_MemberId(Long roomId, Long memberId);

    List<ChatParticipantEntity> findByMember_MemberId(Long memberMemberId);
}