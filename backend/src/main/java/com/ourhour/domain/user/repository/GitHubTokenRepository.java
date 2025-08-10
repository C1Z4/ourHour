package com.ourhour.domain.user.repository;

import com.ourhour.domain.user.entity.GitHubTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubTokenRepository extends JpaRepository<GitHubTokenEntity, Long> {
}