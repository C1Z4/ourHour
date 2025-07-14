package com.ourhour.domain.org.repository;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
