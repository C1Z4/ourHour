package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.entity.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("SELECT  NEW com.ourhour.domain.chat.dto.ChatMessageResDTO(" +
            "       m.chatRoomEntity.roomId, " +
            "       m.chatMessageId, " +
            "       m.memberEntity.memberId, " +
            "       m.memberEntity.name, " +
            "       m.content, " +
            "       m.sentAt) " +
            "FROM   ChatMessageEntity m " +
            "WHERE  m.chatRoomEntity.orgEntity.orgId = :orgId AND m.chatRoomEntity.roomId = :roomId " +
            "ORDER BY m.sentAt DESC")
    Page<ChatMessageResDTO> findAllByOrgAndChatRoom(@Param("orgId") Long orgId, @Param("roomId") Long roomId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ChatMessageEntity cm WHERE cm.chatRoomEntity.roomId = :roomId")
    void deleteAllByChatRoomEntity_RoomId(@Param("roomId") Long roomId);
}