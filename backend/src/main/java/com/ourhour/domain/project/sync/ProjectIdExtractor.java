package com.ourhour.domain.project.sync;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;

/**
 * 엔티티에서 프로젝트 ID를 추출하는 전략 인터페이스
 */
public interface ProjectIdExtractor<T extends GitHubSyncableEntity> {
    Long extractProjectId(T entity);

    Class<T> getSupportedEntityType();
}
