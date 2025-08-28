package com.ourhour.domain.user.repository;

import com.ourhour.domain.user.entity.UserGitHubTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGitHubTokenRepository extends JpaRepository<UserGitHubTokenEntity, Long> {
    
    Optional<UserGitHubTokenEntity> findByUserId(Long userId);
    
    Optional<UserGitHubTokenEntity> findByGithubUsername(String githubUsername);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByGithubUsername(String githubUsername);
}