package com.ourhour.domain.member.repository;

import com.ourhour.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    /* 회원별 회사 목록 조회 */
    @Query("SELECT opm FROM OrgParticipantMemberEntity opm "
            + "JOIN FETCH opm.memberEntity m "
            + "JOIN FETCH m.userEntity u "
            + "JOIN FETCH opm.orgEntity o "
            + "LEFT JOIN FETCH opm.departmentEntity d "
            + "LEFT JOIN FETCH opm.positionEntity p "
            + "WHERE m.memberId = :memberId")
    List<OrgParticipantMemberEntity> findOrgListByMemberId(Long memberId);
}
