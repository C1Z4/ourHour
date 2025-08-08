package com.ourhour.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.enums.IssueStatus;

public interface IssueRepository extends JpaRepository<IssueEntity, Long> {

    Page<IssueEntity> findByMilestoneEntity_MilestoneId(Long milestoneId, Pageable pageable);

    Page<IssueEntity> findByProjectEntity_ProjectIdAndMilestoneEntityIsNull(Long projectId, Pageable pageable);

    long countByMilestoneEntity_MilestoneId(Long milestoneId);

    long countByMilestoneEntity_MilestoneIdAndStatus(Long milestoneId, IssueStatus status);
}
