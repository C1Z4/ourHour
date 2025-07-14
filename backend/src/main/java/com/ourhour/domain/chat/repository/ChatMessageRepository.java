package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("SELECT  NEW com.ourhour.domain.chat.dto.ChatMessageResDTO(" +
            "       m.chatMessageId, " +
            "       m.sender.memberId, " +
            "       m.sender.name, " +
            "       m.content, " +
            "       m.sentAt) " +
            "FROM   ChatMessageEntity m " +
            "WHERE  m.chatRoom.orgEntity.orgId = :orgId AND m.chatRoom.roomId = :roomId " +
            "ORDER BY m.sentAt ASC")
    List<ChatMessageResDTO> findAllByOrgAndChatRoom(@Param("orgId") Long orgId, @Param("roomId") Long roomId);
}