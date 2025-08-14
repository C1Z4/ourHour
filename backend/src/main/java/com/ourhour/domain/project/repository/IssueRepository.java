package com.ourhour.domain.project.repository;

import java.util.List;
import java.util.Optional;

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

    Optional<IssueEntity> findByProjectEntity_ProjectIdAndGithubId(Long projectId, Long githubId);

    List<IssueEntity> findByProjectEntity_ProjectId(Long projectId);

    Page<IssueEntity> findByProjectEntity_ProjectIdAndAssigneeEntity_MemberId(Long projectId, Long memberId,
            Pageable pageable);

    Page<IssueEntity> findByMilestoneEntity_MilestoneIdAndAssigneeEntity_MemberId(Long milestoneId, Long memberId, 
            Pageable pageable);

    Page<IssueEntity> findByProjectEntity_ProjectIdAndMilestoneEntityIsNullAndAssigneeEntity_MemberId(Long projectId, Long memberId, 
            Pageable pageable);

}
