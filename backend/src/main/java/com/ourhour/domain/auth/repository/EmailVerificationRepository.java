package com.ourhour.domain.auth.repository;

import com.ourhour.domain.auth.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {
}
