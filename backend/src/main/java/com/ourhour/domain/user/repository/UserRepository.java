package com.ourhour.domain.user.repository;

import com.ourhour.domain.user.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(@NotBlank(message = "이메일은 필수입니다.") @Email String email);
}
