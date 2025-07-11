package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findAllByChatRoom_RoomId(Long roomId);
}
