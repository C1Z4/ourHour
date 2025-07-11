package com.ourhour.domain.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhour.domain.project.entity.MilestoneEntity;

public interface MilestoneRepository extends JpaRepository<MilestoneEntity, Long> {

    Page<MilestoneEntity> findByProjectEntity_ProjectId(Long projectId, Pageable pageable);
}
