package com.ourhour.domain.member.repository;

import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    /* 본인이 속한 회사 목록 조회 */
    @Query("SELECT opm FROM OrgParticipantMemberEntity opm "
            + "JOIN FETCH opm.memberEntity m "
            + "JOIN FETCH opm.orgEntity o "
            + "LEFT JOIN FETCH opm.departmentEntity d "
            + "LEFT JOIN FETCH opm.positionEntity p "
            + "WHERE m.memberId = :memberId")
    List<OrgParticipantMemberEntity> findOrgListByMemberId(Long memberId);

    /* 본인이 속한 회사 상세 정보 조회 */
    @Query("SELECT opm FROM OrgParticipantMemberEntity opm "
            + "JOIN FETCH opm.memberEntity m "
            + "JOIN FETCH opm.orgEntity o "
            + "LEFT JOIN FETCH opm.departmentEntity d "
            + "LEFT JOIN FETCH opm.positionEntity p "
            + "WHERE m.memberId = :memberId AND o.orgId = :orgId")
    Optional<OrgParticipantMemberEntity> findOrgDetailByMemberIdAndOrgId(Long memberId, Long orgId);


    List<MemberEntity> findByUserEntity_UserId(Long userId);

    /* 본인이 속한 회사 내 자기 자신의 참여 정보 찾기 */
    @Query("""
        select opm.memberEntity
        from OrgParticipantMemberEntity opm
        where opm.orgEntity.orgId = :orgId
          and opm.memberEntity.userEntity.userId = :userId
          and opm.status = com.ourhour.domain.org.enums.Status.ACTIVE
    """)
    Optional<MemberEntity> findMemberInOrgByUserId(@Param("orgId")Long orgId, @Param("userId")Long actingUserId);
}
