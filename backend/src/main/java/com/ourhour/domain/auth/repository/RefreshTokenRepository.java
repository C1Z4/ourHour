package com.ourhour.domain.auth.repository;

import com.ourhour.domain.auth.entity.RefreshTokenEntity;
import com.ourhour.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUserEntity(UserEntity userEntity);
}
