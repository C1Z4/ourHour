package com.ourhour.domain.project.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.project.service.ProjectGitHubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubSyncManager {

    // 엔티티 타입에 따른 동기화 핸들러 매핑
    private final Map<Class<?>, GitHubSyncHandler<?>> syncHandlers = new HashMap<>();
    // 엔티티 타입에 따른 프로젝트 ID 추출기 매핑
    private final Map<Class<?>, ProjectIdExtractor<?>> projectIdExtractors = new HashMap<>();

    private final ProjectGitHubService projectGitHubService;
    private final List<ProjectIdExtractor<?>> extractorList;

    private final IssueSyncHandler issueSyncHandler;
    private final MilestoneSyncHandler milestoneSyncHandler;
    private final IssueCommentSyncHandler issueCommentSyncHandler;

    // 동기화 핸들러 및 추출기 초기화
    @PostConstruct
    private void initializeSyncHandlers() {
        syncHandlers.put(IssueEntity.class, issueSyncHandler);
        syncHandlers.put(MilestoneEntity.class, milestoneSyncHandler);
        // 댓글은 이슈에 귀속되므로 CommentEntity도 등록
        syncHandlers.put(CommentEntity.class, issueCommentSyncHandler);

        // 프로젝트 ID 추출기 초기화
        for (ProjectIdExtractor<?> extractor : extractorList) {
            projectIdExtractors.put(extractor.getSupportedEntityType(), extractor);
        }
    }

    // 엔티티 동기화
    public <T extends GitHubSyncableEntity> void syncToGitHub(T entity, SyncOperation operation) {
        if (!shouldSync(entity)) {
            log.debug("GitHub 동기화 건너뛰기 - 연동되지 않은 프로젝트: {}", entity.getClass().getSimpleName());
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            GitHubSyncHandler<T> handler = (GitHubSyncHandler<T>) syncHandlers.get(entity.getClass());

            if (handler == null) {
                log.error("GitHub 동기화 핸들러를 찾을 수 없습니다. 엔티티: {}", entity.getClass().getSimpleName());
                return;
            }

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

    // 동기화 여부 확인
    private <T extends GitHubSyncableEntity> boolean shouldSync(T entity) {
        Long projectId = getProjectId(entity);
        return projectId != null && isProjectGitHubSynced(projectId);
    }

    // 프로젝트 ID 조회 - 전략 패턴 사용
    @SuppressWarnings("unchecked")
    private <T extends GitHubSyncableEntity> Long getProjectId(T entity) {
        ProjectIdExtractor<T> extractor = (ProjectIdExtractor<T>) projectIdExtractors.get(entity.getClass());
        if (extractor == null) {
            log.warn("프로젝트 ID 추출기를 찾을 수 없습니다. 엔티티: {}", entity.getClass().getSimpleName());
            return null;
        }
        return extractor.extractProjectId(entity);
    }

    // 프로젝트 GitHub 연동 여부 확인
    private boolean isProjectGitHubSynced(Long projectId) {
        return projectGitHubService.hasGitHubIntegration(projectId);
    }
}
