package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.dto.ChatRoomListResDTO;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, ChatParticipantId> {

    @Query(value = "SELECT NEW com.ourhour.domain.chat.dto.ChatRoomListResDTO(" +
            "           cr.roomId, " +
            "           cr.name, " +
            "           cr.color, " +
            "           cm.content, " +
            "           cm.sentAt) " +
            "FROM ChatParticipantEntity cp " +
            "JOIN cp.chatRoomEntity cr " +
            "LEFT JOIN ChatMessageEntity cm ON cm.chatRoomEntity = cr AND cm.chatMessageId = (" +
            "    SELECT MAX(cm2.chatMessageId) " +
            "    FROM ChatMessageEntity cm2 " +
            "    WHERE cm2.chatRoomEntity = cr" +
            ") " +
            "WHERE cp.memberEntity.memberId = :memberId " +
            "AND cr.orgEntity.orgId = :orgId " +
            "ORDER BY COALESCE(cm.sentAt, cr.createdAt) DESC",
            countQuery = "SELECT COUNT(cp) FROM ChatParticipantEntity cp WHERE cp.memberEntity.memberId = :memberId AND cp.chatRoomEntity.orgEntity.orgId = :orgId")
    Page<ChatRoomListResDTO> findChatRoomsWithLastMessage(@Param("orgId") Long orgId, @Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT DISTINCT cp FROM ChatParticipantEntity cp " +
            "JOIN FETCH cp.memberEntity " +
            "WHERE cp.chatRoomEntity.orgEntity.orgId = :orgId AND cp.chatRoomEntity.roomId = :roomId")
    List<ChatParticipantEntity> findParticipantsByOrgAndRoom(@Param("orgId") Long orgId, @Param("roomId") Long roomId);

    @Query("SELECT cp FROM ChatParticipantEntity cp " +
            "WHERE cp.chatRoomEntity.orgEntity.orgId = :orgId " +
            "AND cp.chatRoomEntity.roomId = :roomId " +
            "AND cp.memberEntity.memberId = :memberId")
    Optional<ChatParticipantEntity> findParticipantToDelete(
                                                             @Param("orgId") Long orgId,
                                                             @Param("roomId") Long roomId,
                                                             @Param("memberId") Long memberId
    );
}