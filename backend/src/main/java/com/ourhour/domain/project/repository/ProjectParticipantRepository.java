package com.ourhour.domain.project.repository;

import com.ourhour.domain.project.dto.ProjectNameResDTO;
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
        @Query("SELECT DISTINCT p FROM ProjectParticipantEntity p " +
                        "JOIN FETCH p.memberEntity m " +
                        "WHERE p.projectParticipantId.projectId = :projectId " +
                        "ORDER BY p.projectParticipantId.memberId LIMIT :limit")
        List<ProjectParticipantEntity> findLimitedParticipants(@Param("projectId") Long projectId,
                        @Param("limit") int limit);

        // 프로젝트 참여자 목록 조회
        @Query("SELECT p FROM ProjectParticipantEntity p WHERE p.projectParticipantId.projectId = :projectId")
        List<ProjectParticipantEntity> findByProjectId(@Param("projectId") Long projectId);

        // 프로젝트 참여자 목록 조회 (페이징)
        Page<ProjectParticipantEntity> findByProjectParticipantId_ProjectId(Long projectId, Pageable pageable);

        // 프로젝트 참여자 이름 검색 조회 (페이징)
        @Query("SELECT p FROM ProjectParticipantEntity p " +
                        "JOIN p.memberEntity m " +
                        "WHERE p.projectParticipantId.projectId = :projectId " +
                        "AND m.name LIKE CONCAT('%', :search, '%') " +
                        "ORDER BY p.projectParticipantId.memberId ASC")
        Page<ProjectParticipantEntity> findByProjectParticipantId_ProjectIdAndMemberNameContaining(
                        @Param("projectId") Long projectId, @Param("search") String search, Pageable pageable);

        // 프로젝트 ID로 전체 참여자 삭제
        @Modifying
        @Query("DELETE FROM ProjectParticipantEntity p WHERE p.projectParticipantId.projectId = :projectId")
        void deleteByProjectParticipantId_ProjectId(@Param("projectId") Long projectId);

        // 특정 멤버가 특정 조직에서 참여 중인 프로젝트 이름 목록 조회
        @Query("SELECT new com.ourhour.domain.project.dto.ProjectNameResDTO(" +
                        "proj.projectId, proj.name) " +
                        "FROM ProjectParticipantEntity pp " +
                        "JOIN pp.projectEntity proj " +
                        "WHERE pp.projectParticipantId.memberId = :memberId " +
                        "AND proj.orgEntity.orgId = :orgId " +
                        "ORDER BY proj.projectId ASC")
        List<ProjectNameResDTO> findMemberProjectsByOrg(@Param("memberId") Long memberId, @Param("orgId") Long orgId);
}