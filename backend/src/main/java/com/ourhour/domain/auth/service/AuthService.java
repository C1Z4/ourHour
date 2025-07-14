package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.auth.repository.RefreshTokenRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.mapper.UserMapper;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ourhour.domain.auth.exception.AuthException.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncode;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;

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
        UserEntity userEntity = userMapper.toUserEntity(signupReqDTO, hashedPassword , LocalDateTime.now());

        userRepository.save(userEntity);

    }

    @Transactional
    public void signin (SignupReqDTO signupReqDTO) {

        // 이메일로 사용자 조회
        UserEntity userEntity = userRepository.findByEmail(signupReqDTO.getEmail())
                .orElseThrow(AuthException::emailNotFoundException);

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signupReqDTO.getPassword(), userEntity.getPassword())) {
            throw invalidPasswordException();
        }

        // JWT 발급

        // refresh token 저장

        // access token 반환

    }
}
