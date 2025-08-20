package com.ourhour.domain.user.repository;

import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.enums.Platform;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndIsDeletedFalse(@NotBlank(message = "이메일은 필수입니다.") @Email String email);

    Optional<UserEntity> findByUserIdAndIsDeletedFalse(Long userId);

    boolean existsByEmailAndIsDeletedFalse(@NotBlank(message = "이메일은 필수입니다.") @Email String email);

    Optional<UserEntity> findByPlatformAndOauthId(Platform platform, String oauthId);
}
