package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.OrgInvBatchEntity;
import com.ourhour.domain.org.entity.OrgInvEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrgInvBatchRepository extends JpaRepository<OrgInvBatchEntity, Long> {
    Long findOrgIdByBatchId(Long batchId);

    List<OrgInvBatchEntity> findAllByOrgId(Long orgId);
}
