package com.ourhour.domain.org.repository;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrgParticipantMemberRepository
        extends JpaRepository<OrgParticipantMemberEntity, OrgParticipantMemberId> {

    @Query("SELECT opm " +
            "FROM OrgParticipantMemberEntity opm " +
            "JOIN opm.memberEntity m " +
            "LEFT JOIN opm.positionEntity p " +
            "LEFT JOIN opm.departmentEntity d " +
            "WHERE opm.orgEntity.orgId = :orgId " +
            "ORDER BY m.memberId ASC")
    Page<OrgParticipantMemberEntity> findByOrgId(@Param("orgId") Long orgId, Pageable pageable);

    @Query("SELECT opm " +
            "FROM OrgParticipantMemberEntity opm " +
            "JOIN opm.memberEntity m " +
            "LEFT JOIN opm.positionEntity p " +
            "LEFT JOIN opm.departmentEntity d " +
            "WHERE opm.orgEntity.orgId = :orgId " +
            "AND m.name LIKE %:search% " +
            "ORDER BY m.memberId ASC")
    Page<OrgParticipantMemberEntity> findByOrgIdAndNameContaining(@Param("orgId") Long orgId, @Param("search") String search, Pageable pageable);

    boolean existsByOrgEntity_OrgIdAndMemberEntity_MemberId(Long orgId, Long memberId);

    @Query("SELECT opm " +
            "FROM OrgParticipantMemberEntity opm " +
            "JOIN opm.memberEntity m " +
            "LEFT JOIN opm.positionEntity p " +
            "LEFT JOIN opm.departmentEntity d " +
            "WHERE opm.orgEntity.orgId = :orgId " +
            "AND opm.memberEntity.memberId=:memberId ")
    MemberInfoResDTO findByOrgIdAndMemberId(@Param("orgId") Long orgId, @Param("memberId") Long memberId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update OrgParticipantMemberEntity opm
           set opm.status = :inactive,
               opm.leftAt = :leftAt
         where opm.memberEntity in :members
           and opm.status = :active
    """)
    void updateDeactivateAllMembers(@Param("members") List<MemberEntity> memberEntity,
                                    @Param("inactive") Status inactive,
                                    @Param("leftAt") LocalDate leftAt,
                                    @Param("active") Status active
    );

    Optional<OrgParticipantMemberEntity> findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(Long orgId, Long memberId, Status status);

    // 해당 회사의 권한에 따른 멤버 수 집계
    int countByOrgEntity_OrgIdAndRole(Long orgId, Role role);

    // 권한이 루트 관리자인 멤버만 집계
    default int countRootAdmins(Long orgId) {
        return countByOrgEntity_OrgIdAndRole(orgId, Role.ROOT_ADMIN);
    }

    List<OrgParticipantMemberEntity> findAllByMemberEntity_MemberIdAndStatus(Long memberId, Status status);

    boolean existsByOrgEntity_OrgIdAndMemberEntity_MemberIdAndRoleAndStatus(Long orgId, Long memberId, Role role, Status status);

    OrgParticipantMemberEntity findByOrgEntity_OrgIdAndMemberEntity_MemberId(Long orgEntityOrgId, Long memberEntityMemberId);

    // 조직 내 활성 루트 관리자 수 조회
    int countByOrgEntity_OrgIdAndRoleAndStatus(Long orgId, Role role, Status status);

    boolean existsByOrgEntityAndMemberEntity(OrgEntity orgEntity, MemberEntity memberToJoin);

    Optional<OrgParticipantMemberEntity> findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus(Long orgId, Long userId, Status status);

    @Query("SELECT opm " +
            "FROM OrgParticipantMemberEntity opm " +
            "JOIN opm.memberEntity m " +
            "LEFT JOIN opm.positionEntity p " +
            "LEFT JOIN opm.departmentEntity d " +
            "WHERE opm.orgEntity.orgId = :orgId " +
            "ORDER BY m.memberId ASC")
   List<OrgParticipantMemberEntity> findAllByOrgEntity_OrgId(Long orgId);

    boolean existsByOrgEntity_OrgIdAndMemberEntity_Email(Long orgId, String email);
}
