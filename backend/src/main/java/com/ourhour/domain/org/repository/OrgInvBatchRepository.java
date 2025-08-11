package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.OrgInvBatchEntity;
import com.ourhour.domain.org.entity.OrgInvEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrgInvBatchRepository extends JpaRepository<OrgInvBatchEntity, Long> {
    @Query("""
        select oivb.orgParticipantMemberEntity.orgEntity.orgId
        from OrgInvBatchEntity oivb
        join oivb.orgParticipantMemberEntity opm
        where oivb.batchId=:batchId
    """)
    Long findOrgIdByBatchId(Long batchId);

    @Query("""
        select oivb
        from OrgInvBatchEntity oivb
        join oivb.orgParticipantMemberEntity opm
        join opm.orgEntity o
        where o.orgId = :orgId
    """)
    List<OrgInvBatchEntity> findAllByOrgId(Long orgId);
}
