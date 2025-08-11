package com.ourhour.domain.project.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.enums.SyncStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubSyncDomainService {

    // 엔티티를 동기화 완료 상태로 변경
    public <T extends GitHubSyncableEntity> void markAsSynced(T entity, Long githubId) {
        entity.setGithubId(githubId);
        entity.setIsGithubSynced(true);
        entity.setLastSyncedAt(LocalDateTime.now());
        entity.setSyncStatus(SyncStatus.SYNCED);

        log.info("GitHub 동기화 완료 - Entity: {}, ID: {}, GitHub ID: {}",
                entity.getClass().getSimpleName(), entity.getId(), githubId);
    }

    // 동기화 실패 처리
    public <T extends GitHubSyncableEntity> void markSyncFailed(T entity, String errorMessage) {
        entity.setSyncStatus(SyncStatus.SYNC_FAILED);

        log.error("GitHub 동기화 실패 - Entity: {}, ID: {}, Error: {}",
                entity.getClass().getSimpleName(), entity.getId(), errorMessage);

        // 추가적인 실패 처리 로직 (재시도 스케줄링 등)
        scheduleRetry(entity);
    }

    // 동기화 필요성 확인
    public <T extends GitHubSyncableEntity> boolean needsSync(T entity) {
        if (!entity.getIsGithubSynced()) {
            return false;
        }

        if (entity.getLastSyncedAt() == null) {
            return true;
        }

        return entity.getUpdatedAt() != null &&
                entity.getUpdatedAt().isAfter(entity.getLastSyncedAt());
    }

    // 연동 해제
    public <T extends GitHubSyncableEntity> void unlink(T entity) {
        entity.setGithubId(null);
        entity.setIsGithubSynced(false);
        entity.setLastSyncedAt(null);
        entity.setGithubEtag(null);
        entity.setSyncStatus(SyncStatus.NOT_SYNCED);

        log.info("GitHub 연동 해제 - Entity: {}, ID: {}",
                entity.getClass().getSimpleName(), entity.getId());
    }

    // 동기화 상태 체크 및 복구
    public <T extends GitHubSyncableEntity> void validateSyncStatus(T entity) {
        if (entity.getIsGithubSynced() && entity.getGithubId() == null) {
            // 불일치 상태 감지
            log.warn("동기화 상태 불일치 감지 - Entity: {}, ID: {}",
                    entity.getClass().getSimpleName(), entity.getId());
            entity.setSyncStatus(SyncStatus.CONFLICT);
        }
    }

    private <T extends GitHubSyncableEntity> void scheduleRetry(T entity) {
        // 재시도 로직 구현 (예: 큐에 추가, 스케줄러 등록 등)
    }
}
