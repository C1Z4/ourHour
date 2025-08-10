package com.ourhour.domain.project.sync;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;
import com.ourhour.domain.project.service.ProjectGitHubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubSyncManager {

    private final Map<Class<?>, GitHubSyncHandler<?>> syncHandlers = new HashMap<>();
    private final ProjectGitHubService projectGitHubService;
    private final ProjectGithubIntegrationRepository integrationRepository;

    private final IssueSyncHandler issueSyncHandler;

    @PostConstruct
    private void initializeSyncHandlers() {
        syncHandlers.put(IssueEntity.class, issueSyncHandler);
        // 다른 엔티티들 추가 예정
    }

    public <T extends GitHubSyncableEntity> void syncToGitHub(T entity, SyncOperation operation) {
        if (!shouldSync(entity)) {
            log.debug("GitHub 동기화 건너뛰기 - 연동되지 않은 프로젝트: {}", entity.getClass().getSimpleName());
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            GitHubSyncHandler<T> handler = (GitHubSyncHandler<T>) syncHandlers.get(entity.getClass());

            switch (operation) {
                case CREATE:
                    handler.createInGitHub(entity);
                    break;
                case UPDATE:
                    if (entity.getIsGithubSynced()) {
                        handler.updateInGitHub(entity);
                    } else if (isProjectGitHubSynced(getProjectId(entity))) {
                        // 연동되지 않은 기존 엔티티를 GitHub에 생성
                        handler.createInGitHub(entity);
                    }
                    break;
                case DELETE:
                    if (entity.getIsGithubSynced()) {
                        handler.deleteInGitHub(entity);
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("GitHub 동기화 실패 - Operation: {}, Entity: {}, ID: {}",
                    operation, entity.getClass().getSimpleName(), entity.getId(), e);
        }
    }

    private <T extends GitHubSyncableEntity> boolean shouldSync(T entity) {
        Long projectId = getProjectId(entity);
        return projectId != null && isProjectGitHubSynced(projectId);
    }

    private <T extends GitHubSyncableEntity> Long getProjectId(T entity) {
        if (entity instanceof IssueEntity) {
            return ((IssueEntity) entity).getProjectEntity().getProjectId();
        }
        // 다른 엔티티 타입들 추가
        return null;
    }

    private boolean isProjectGitHubSynced(Long projectId) {
        return projectGitHubService.hasGitHubIntegration(projectId);
    }

    private <T extends GitHubSyncableEntity> GitHubSyncHandler<T> getSyncHandler(Class<T> entityClass) {
        GitHubSyncHandler<T> handler = (GitHubSyncHandler<T>) syncHandlers.get(entityClass);
        if (handler == null) {
            throw new IllegalArgumentException("지원하지 않는 엔티티 타입: " + entityClass.getSimpleName());
        }
        return handler;
    }
}
