package com.ourhour.domain.project.repository;

import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.entity.ProjectParticipantId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipantEntity, ProjectParticipantId> {

    // 제한된 참여자 목록 조회
    @Query("SELECT p FROM ProjectParticipantEntity p WHERE p.projectParticipantId.projectId = :projectId ORDER BY p.projectParticipantId.memberId LIMIT :limit")
    List<ProjectParticipantEntity> findLimitedParticipants(@Param("projectId") Long projectId, @Param("limit") int limit);

    // 프로젝트 참여자 목록 조회 
    @Query("SELECT p FROM ProjectParticipantEntity p WHERE p.projectParticipantId.projectId = :projectId")
    List<ProjectParticipantEntity> findByProjectId(@Param("projectId") Long projectId);

    // 프로젝트 참여자 목록 조회 (페이징)
    Page<ProjectParticipantEntity> findByProjectParticipantId_ProjectId(Long projectId, Pageable pageable);

    // 프로젝트 ID로 전체 참여자 삭제
    @Modifying
    @Query("DELETE FROM ProjectParticipantEntity p WHERE p.projectParticipantId.projectId = :projectId")
    void deleteByProjectParticipantId_ProjectId(@Param("projectId") Long projectId);
}