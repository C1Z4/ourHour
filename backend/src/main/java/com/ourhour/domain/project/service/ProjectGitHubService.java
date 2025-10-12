package com.ourhour.domain.project.service;

import org.springframework.stereotype.Service;

import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 프로젝트 GitHub 연동 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectGitHubService {

    private final ProjectGithubIntegrationRepository integrationRepository;

    /**
     * 프로젝트의 GitHub 연동 여부 확인
     */
    public boolean hasGitHubIntegration(Long projectId) {
        return integrationRepository.existsByProjectEntity_ProjectIdAndIsActive(projectId, true);
    }

    /**
     * 프로젝트의 GitHub 연동 정보 조회
     */
    public Optional<ProjectGithubIntegrationEntity> getGitHubIntegration(Long projectId) {
        return integrationRepository.findByProjectEntity_ProjectIdAndIsActive(projectId, true);
    }

    /**
     * GitHub 연동 활성화
     */
    public void activateGitHubIntegration(Long projectId) {
        integrationRepository.findByProjectEntity_ProjectId(projectId)
                .ifPresent(integration -> {
                    integration.activate();
                    integrationRepository.save(integration);
                    log.info("GitHub integration activated for project: {}", projectId);
                });
    }

    /**
     * GitHub 연동 비활성화
     */
    public void deactivateGitHubIntegration(Long projectId) {
        integrationRepository.findByProjectEntity_ProjectId(projectId)
                .ifPresent(integration -> {
                    integration.deactivate();
                    integrationRepository.save(integration);
                    log.info("GitHub integration deactivated for project: {}", projectId);
                });
    }

    /**
     * GitHub 저장소 정보 조회
     */
    public Optional<String> getGitHubRepositoryUrl(Long projectId) {
        return getGitHubIntegration(projectId)
                .map(integration -> "https://github.com/" + integration.getGithubRepository());
    }
}