package com.ourhour.domain.org.repository;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface OrgRepository extends JpaRepository<OrgEntity, Long> {

    List<MemberInfoResDTO> findByOrgId(Long orgId);
}
