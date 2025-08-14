package com.ourhour.domain.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.ourhour.domain.project.entity.MilestoneEntity;

public interface MilestoneRepository extends JpaRepository<MilestoneEntity, Long> {

       Page<MilestoneEntity> findByProjectEntity_ProjectId(Long projectId, Pageable pageable);

       Optional<MilestoneEntity> findByProjectEntity_ProjectIdAndName(Long projectId, String name);

       Optional<MilestoneEntity> findByProjectEntity_ProjectIdAndGithubId(Long projectId, Long githubId);

       List<MilestoneEntity> findByProjectEntity_ProjectId(Long projectId);

       @Query("SELECT DISTINCT m FROM MilestoneEntity m " +
                     "JOIN m.issueEntityList i " +
                     "WHERE m.projectEntity.projectId = :projectId " +
                     "AND i.assigneeEntity.memberId = :memberId")
       Page<MilestoneEntity> findByProjectEntity_ProjectIdWithAssignedIssues(@Param("projectId") Long projectId,
                     @Param("memberId") Long memberId,
                     Pageable pageable);

}
