package com.ourhour.domain.project.sync;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;

public interface GitHubSyncHandler<T extends GitHubSyncableEntity> {

    void createInGitHub(T entity);
    void updateInGitHub(T entity);
    void deleteInGitHub(T entity);
}
