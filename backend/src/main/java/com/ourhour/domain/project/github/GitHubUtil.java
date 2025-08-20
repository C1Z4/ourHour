package com.ourhour.domain.project.github;

import org.springframework.stereotype.Component;

import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitHubUtil {
    private final ProjectGithubIntegrationRepository integrationRepository;

    public ProjectGithubIntegrationEntity getActiveIntegration(Long projectId) {
        return integrationRepository.findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                .orElseThrow(() -> GithubException.githubIntegrationNotFoundException());
    }
}
