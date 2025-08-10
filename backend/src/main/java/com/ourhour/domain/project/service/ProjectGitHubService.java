package com.ourhour.domain.project.service;

import org.springframework.stereotype.Service;

import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectGitHubService {

    private final ProjectGithubIntegrationRepository integrationRepository;

    public boolean hasGitHubIntegration(ProjectEntity project) {
        return integrationRepository.existsByProjectEntity_ProjectIdAndIsActive(project.getProjectId(), true);
    }

    public boolean hasGitHubIntegration(Long projectId) {
        return integrationRepository.existsByProjectEntity_ProjectIdAndIsActive(projectId, true);
    }
}