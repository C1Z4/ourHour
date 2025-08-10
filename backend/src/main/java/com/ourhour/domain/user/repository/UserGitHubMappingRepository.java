package com.ourhour.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import com.ourhour.domain.user.entity.UserGitHubMappingEntity;

@Repository
public interface UserGitHubMappingRepository extends JpaRepository<UserGitHubMappingEntity, Long> {
    Optional<UserGitHubMappingEntity> findByUserId(Long userId);

    @Transactional
    long deleteByUserId(Long userId);
}
