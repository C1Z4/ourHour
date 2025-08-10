package com.ourhour.domain.project.sync;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;

public interface GitHubSyncHandler<T extends GitHubSyncableEntity> {

    /**
     * GitHub에 엔티티 생성
     * 
     * @param entity 생성할 엔티티
     */
    void createInGitHub(T entity);

    /**
     * GitHub의 엔티티 업데이트
     * 
     * @param entity 업데이트할 엔티티
     */
    void updateInGitHub(T entity);

    /**
     * GitHub의 엔티티 삭제
     * 
     * @param entity 삭제할 엔티티
     */
    void deleteInGitHub(T entity);
}
