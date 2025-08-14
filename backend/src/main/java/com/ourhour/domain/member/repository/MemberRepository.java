package com.ourhour.domain.member.repository;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.sql.Struct;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    /* 본인이 속한 회사 목록 조회 */
    @Query("SELECT opm FROM OrgParticipantMemberEntity opm "
            + "JOIN opm.memberEntity m "
            + "JOIN opm.orgEntity o "
            + "LEFT JOIN opm.departmentEntity d "
            + "LEFT JOIN opm.positionEntity p "
            + "WHERE m.memberId = :memberId")
    Page<OrgParticipantMemberEntity> findOrgListByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    /* 본인이 속한 모든 회사 목록 조회 (여러 memberId) */
    @Query("SELECT opm FROM OrgParticipantMemberEntity opm "
            + "JOIN opm.memberEntity m "
            + "JOIN opm.orgEntity o "
            + "LEFT JOIN opm.departmentEntity d "
            + "LEFT JOIN opm.positionEntity p "
            + "WHERE m.memberId IN :memberIds AND opm.status = com.ourhour.domain.org.enums.Status.ACTIVE")
    Page<OrgParticipantMemberEntity> findOrgListByMemberIdsAndStatus(@Param("memberIds") List<Long> memberIds, @Param("status") Status status, Pageable pageable);

    /* userId로 모든 memberId 조회 */
    @Query("SELECT DISTINCT m.memberId FROM MemberEntity m WHERE m.userEntity.userId = :userId")
    List<Long> findAllMemberIdsByUserId(@Param("userId") Long userId);

   /* 본인이 속한 회사 조회 */
    @Query("SELECT opm FROM OrgParticipantMemberEntity opm "
            + "JOIN FETCH opm.memberEntity m "
            + "JOIN FETCH opm.orgEntity o "
            + "LEFT JOIN FETCH opm.departmentEntity d "
            + "LEFT JOIN FETCH opm.positionEntity p "
            + "WHERE m.memberId = :memberId")
    OrgParticipantMemberEntity findOrgByMemberId(Long memberId);

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

    Optional<MemberEntity> findByUserEntity_UserIdAndEmail(Long userId, String invitedEmail);

}
