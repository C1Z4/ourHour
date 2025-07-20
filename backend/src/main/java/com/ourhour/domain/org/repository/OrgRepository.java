package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.OrgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgRepository extends JpaRepository<OrgEntity, Long> {

    OrgEntity findByOrgId(Long orgId);
}
