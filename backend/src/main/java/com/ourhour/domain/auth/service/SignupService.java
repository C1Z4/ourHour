package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.enums.Platform;
import com.ourhour.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ourhour.domain.auth.exception.AuthException.duplicateRequestException;
import static com.ourhour.domain.auth.exception.AuthException.emailVerificationException;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncode;
    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    public void signup(SignupReqDTO signupReqDTO) {

        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupReqDTO.getEmail())) {
            throw duplicateRequestException();
        }

        boolean isVerified = emailVerificationRepository.existsByEmailAndIsUsedTrue(signupReqDTO.getEmail());
        if(!isVerified) {
            throw emailVerificationException("이메일 인증 먼저 해주세요.");
        }

        // UserEntity 저장
        String hashedPassword = passwordEncode.encode(signupReqDTO.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .email(signupReqDTO.getEmail())
                .password(hashedPassword)
                .platform(Platform.OURHOUR)
                .isEmailVerified(true)
                .emailVerifiedAt(LocalDateTime.now())
                .build();

        userRepository.save(userEntity);

    }
}
