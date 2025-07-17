package com.ourhour.domain.org.repository;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrgParticipantMemberRepository
        extends JpaRepository<OrgParticipantMemberEntity, OrgParticipantMemberId> {

    @Query("SELECT new com.ourhour.domain.member.dto.MemberInfoResDTO(" +
            "m.memberId, m.name, m.email, m.phone, " +
            "COALESCE(p.name, ''), COALESCE(d.name, ''), m.profileImgUrl) " +
            "FROM OrgParticipantMemberEntity opm " +
            "JOIN opm.memberEntity m " +
            "LEFT JOIN opm.positionEntity p " +
            "LEFT JOIN opm.departmentEntity d " +
            "WHERE opm.orgEntity.orgId = :orgId " +
            "ORDER BY m.memberId ASC")
    Page<MemberInfoResDTO> findByOrgId(@Param("orgId") Long orgId, Pageable pageable);

    boolean existsByOrgEntity_OrgIdAndMemberEntity_MemberId(Long orgId, Long memberId);

    @Query("SELECT new com.ourhour.domain.member.dto.MemberInfoResDTO(" +
            "m.memberId, m.name, m.email, m.phone, " +
            "COALESCE(p.name, ''), COALESCE(d.name, ''), m.profileImgUrl) " +
            "FROM OrgParticipantMemberEntity opm " +
            "JOIN opm.memberEntity m " +
            "LEFT JOIN opm.positionEntity p " +
            "LEFT JOIN opm.departmentEntity d " +
            "WHERE opm.orgEntity.orgId = :orgId " +
            "AND opm.memberEntity.memberId=:memberId ")
    MemberInfoResDTO findByOrgIdAndMemberId(Long orgId, Long memberId);


    Optional<OrgParticipantMemberEntity> findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(Long orgId, Long memberId, Status status);

    // 해당 회사의 권한에 따른 멤버 수 집계
    int countByOrgEntity_OrgIdAndRole(Long orgId, Role role);

    // 권한이 루트 관리자인 멤버만 집계
    default int countRootAdmins(Long orgId) {
        return countByOrgEntity_OrgIdAndRole(orgId, Role.ROOT_ADMIN);
    }
}
