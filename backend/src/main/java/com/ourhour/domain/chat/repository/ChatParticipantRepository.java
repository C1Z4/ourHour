package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, ChatParticipantId> {
}
