package com.ourhour.domain.project.repository;

import com.ourhour.domain.project.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    // 회사 내 프로젝트 목록 조회
    Page<ProjectEntity> findByOrgEntity_OrgId(Long orgId, Pageable pageable);

    @Query("SELECT p.orgEntity.orgId FROM ProjectEntity p WHERE p.projectId = :projectId")
    Long findOrgIdByProjectId(Long projectId);

    // 특정 조직의 프로젝트 중 특정 멤버가 참여한 프로젝트만 조회
    @Query("SELECT DISTINCT p FROM ProjectEntity p " +
           "JOIN ProjectParticipantEntity pp ON p.projectId = pp.projectParticipantId.projectId " +
           "WHERE p.orgEntity.orgId = :orgId AND pp.projectParticipantId.memberId = :memberId")
    Page<ProjectEntity> findByOrgEntity_OrgIdAndParticipantMemberId(Long orgId, Long memberId, Pageable pageable);
}
