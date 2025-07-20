package com.ourhour.domain.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.ourhour.domain.project.entity.MilestoneEntity;

public interface MilestoneRepository extends JpaRepository<MilestoneEntity, Long> {

    Page<MilestoneEntity> findByProjectEntity_ProjectId(Long projectId, Pageable pageable);

    Optional<MilestoneEntity> findByProjectEntity_ProjectIdAndName(Long projectId, String name);
}
