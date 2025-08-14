package com.ourhour.domain.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;

public interface ProjectGithubIntegrationRepository extends JpaRepository<ProjectGithubIntegrationEntity, Long> {

        Optional<ProjectGithubIntegrationEntity> findByProjectEntity_ProjectId(Long projectId);

        Optional<ProjectGithubIntegrationEntity> findByProjectEntity_ProjectIdAndMemberEntity_MemberId(Long projectId,
                        Long memberId);

        Optional<ProjectGithubIntegrationEntity> findByProjectEntity_ProjectIdAndIsActive(Long projectId,
                        Boolean isActive);

        Optional<ProjectGithubIntegrationEntity> findByProjectEntity_ProjectIdAndMemberEntity_MemberIdAndIsActive(
                        Long projectId, Long memberId,
                        Boolean isActive);

        boolean existsByProjectEntity_ProjectIdAndIsActive(Long projectId, Boolean isActive);

        boolean existsByProjectEntity_ProjectIdAndMemberEntity_MemberIdAndIsActive(Long projectId, Long memberId,
                        Boolean isActive);
}
