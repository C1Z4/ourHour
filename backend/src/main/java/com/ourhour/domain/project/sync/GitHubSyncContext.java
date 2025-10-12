package com.ourhour.domain.project.sync;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * GitHub 동기화 컨텍스트 객체
 * ThreadLocal 대신 사용하여 메모리 누수 위험을 줄임
 */
@Getter
@Setter
public class GitHubSyncContext {
    private GitHubSyncableEntity pendingDeleteEntity;

    public void clear() {
        this.pendingDeleteEntity = null;
    }

    public boolean hasPendingDeleteEntity() {
        return this.pendingDeleteEntity != null;
    }
}
