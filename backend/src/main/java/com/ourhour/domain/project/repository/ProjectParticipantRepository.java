package com.ourhour.domain.project.repository;

import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.entity.ProjectParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipantEntity, ProjectParticipantId> {

    // 프로젝트 참여자 목록 조회
    @Query(value = "SELECT * FROM tbl_project_participant pp WHERE pp.project_id = :projectId ORDER BY pp.member_id LIMIT :limit", nativeQuery = true)
    List<ProjectParticipantEntity> findLimitedParticipants(@Param("projectId") Long projectId, @Param("limit") int limit);

}