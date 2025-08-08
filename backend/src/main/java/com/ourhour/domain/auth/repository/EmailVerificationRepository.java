package com.ourhour.domain.auth.repository;

import com.ourhour.domain.auth.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {

    // 해당 token 조회
    Optional<EmailVerificationEntity> findByToken(String token);

    // 이메일 인증 성공한 이메일 조회
    boolean existsByEmailAndIsUsedTrue (String email);

    @Modifying
    @Query(value = "UPDATE tbl_email_verification SET is_used = FALSE WHERE email = :email", nativeQuery = true)
    void invalidateByEmail(@Param("email") String email);
}
