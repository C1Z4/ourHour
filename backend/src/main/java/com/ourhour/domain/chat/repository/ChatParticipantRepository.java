package com.ourhour.domain.chat.repository;

import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatParticipantId;
import com.ourhour.domain.chat.entity.ChatRoomEntity;
import com.ourhour.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, ChatParticipantId> {


    @Query("SELECT DISTINCT cp FROM ChatParticipantEntity cp " +
            "JOIN FETCH cp.chatRoomEntity cr " +
            "JOIN OrgParticipantMemberEntity opm ON cp.memberEntity.memberId = opm.memberEntity.memberId " +
            "WHERE opm.orgEntity.orgId = :orgId AND cp.memberEntity.memberId = :memberId " +
            "AND cr.orgEntity.orgId = :orgId")
    List<ChatParticipantEntity> findChatRoomsByOrgAndMember(@Param("orgId") Long orgId, @Param("memberId") Long memberId);

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