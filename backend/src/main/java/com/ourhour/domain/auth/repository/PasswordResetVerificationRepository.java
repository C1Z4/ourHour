package com.ourhour.domain.auth.repository;

import com.ourhour.domain.auth.entity.PasswordResetVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetVerificationRepository extends JpaRepository<PasswordResetVerificationEntity, Long> {

    Optional<PasswordResetVerificationEntity> findByToken(String token);
}
