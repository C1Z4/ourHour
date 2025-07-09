package com.ourhour.domain.project.repository;

import com.ourhour.domain.project.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    // 회사 내 프로젝트 목록 조회
    Page<ProjectEntity> findByOrgEntity_OrgId(Long orgId, Pageable pageable);
}
