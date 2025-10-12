package com.ourhour.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.enums.IssueStatus;

public interface IssueRepository extends JpaRepository<IssueEntity, Long> {

    @Query("SELECT i FROM IssueEntity i WHERE i.milestoneEntity.milestoneId = :milestoneId ORDER BY CASE WHEN i.status = 'COMPLETED' THEN 1 ELSE 0 END, i.createdAt ASC")
    Page<IssueEntity> findByMilestoneEntity_MilestoneId(@Param("milestoneId") Long milestoneId, Pageable pageable);

    @Query("SELECT i FROM IssueEntity i WHERE i.projectEntity.projectId = :projectId AND i.milestoneEntity IS NULL ORDER BY CASE WHEN i.status = 'COMPLETED' THEN 1 ELSE 0 END, i.createdAt ASC")
    Page<IssueEntity> findByProjectEntity_ProjectIdAndMilestoneEntityIsNull(@Param("projectId") Long projectId, Pageable pageable);

    long countByMilestoneEntity_MilestoneId(Long milestoneId);

    long countByMilestoneEntity_MilestoneIdAndStatus(Long milestoneId, IssueStatus status);

    Optional<IssueEntity> findByProjectEntity_ProjectIdAndGithubId(Long projectId, Long githubId);

    List<IssueEntity> findByProjectEntity_ProjectId(Long projectId);

    Page<IssueEntity> findByProjectEntity_ProjectIdAndAssigneeEntity_MemberId(Long projectId, Long memberId,
            Pageable pageable);

    @Query("SELECT i FROM IssueEntity i WHERE i.milestoneEntity.milestoneId = :milestoneId AND i.assigneeEntity.memberId = :memberId ORDER BY CASE WHEN i.status = 'COMPLETED' THEN 1 ELSE 0 END, i.createdAt ASC")
    Page<IssueEntity> findByMilestoneEntity_MilestoneIdAndAssigneeEntity_MemberId(@Param("milestoneId") Long milestoneId, @Param("memberId") Long memberId, 
            Pageable pageable);

    @Query("SELECT i FROM IssueEntity i WHERE i.projectEntity.projectId = :projectId AND i.milestoneEntity IS NULL AND i.assigneeEntity.memberId = :memberId ORDER BY CASE WHEN i.status = 'COMPLETED' THEN 1 ELSE 0 END, i.createdAt ASC")
    Page<IssueEntity> findByProjectEntity_ProjectIdAndMilestoneEntityIsNullAndAssigneeEntity_MemberId(@Param("projectId") Long projectId, @Param("memberId") Long memberId,
            Pageable pageable);

    @Query("SELECT i.milestoneEntity.milestoneId as milestoneId, COUNT(i) as totalCount " +
           "FROM IssueEntity i " +
           "WHERE i.milestoneEntity.milestoneId IN :milestoneIds " +
           "GROUP BY i.milestoneEntity.milestoneId")
    List<MilestoneIssueCount> countByMilestoneIds(@Param("milestoneIds") List<Long> milestoneIds);

    @Query("SELECT i.milestoneEntity.milestoneId as milestoneId, COUNT(i) as completedCount " +
           "FROM IssueEntity i " +
           "WHERE i.milestoneEntity.milestoneId IN :milestoneIds AND i.status = :status " +
           "GROUP BY i.milestoneEntity.milestoneId")
    List<MilestoneIssueCount> countByMilestoneIdsAndStatus(@Param("milestoneIds") List<Long> milestoneIds, @Param("status") IssueStatus status);

    interface MilestoneIssueCount {
        Long getMilestoneId();
        Long getTotalCount();
        Long getCompletedCount();
    }

}
