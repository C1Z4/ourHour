package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, Long> {

    @Query(
            "SELECT p " +
            "FROM   ChatParticipantEntity p " +
            "   JOIN FETCH p.chatRoomEntity " +
            "WHERE p.memberEntity.memberId = :memberId")
    List<ChatParticipantEntity> findAllByMemberIdWithChatRoom(@Param("memberId") Long memberId);

    List<ChatParticipantEntity> findAllByChatRoomEntity_RoomId(Long roomId);
}