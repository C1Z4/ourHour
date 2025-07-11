package com.ourhour.domain.auth.repository;

import com.ourhour.domain.auth.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {

    // 해당 token 조회
    Optional<EmailVerificationEntity> findByToken(String token);

    // 이메일 인증 성공한 이메일 조회
    boolean existsByEmailAndIsUsedTrue (String email);

}
