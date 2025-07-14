package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    Optional<ChatRoomEntity> findByOrgEntity_OrgIdAndRoomId(Long orgId, Long roomId);

    void deleteByOrgEntity_OrgIdAndRoomId(Long orgId, Long roomId);
}
