package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.SigninResDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.entity.RefreshTokenEntity;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.auth.repository.RefreshTokenRepository;
import com.ourhour.domain.auth.util.AuthServiceHelper;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.enums.Platform;
import com.ourhour.domain.user.mapper.UserMapper;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.util.SecurityUtil;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceHelper authServiceHelper;

    // 이메일 중복 확인
    @Transactional(readOnly = true)
    public boolean checkAvailEmail(String email) {

        boolean alreadyHasEmail = userRepository.existsByEmailAndIsDeletedFalse(email);
        if (alreadyHasEmail) {
            return false;
        }

        return true;

    }

    // 회원가입
    @Transactional
    public void signup(SignupReqDTO signupReqDTO) {

        // 이메일 중복 및 탈퇴된 이메일이 아닌지 확인
        if (!checkAvailEmail(signupReqDTO.getEmail())) {
            throw duplicateRequestException();
        }

        boolean isVerified = emailVerificationRepository.existsByEmailAndIsUsedTrue(signupReqDTO.getEmail());
        if (!isVerified) {
            throw emailVerificationRequiredException();
        }

        // UserEntity 저장
        String hashedPassword = passwordEncoder.encode(signupReqDTO.getPassword());
        UserEntity userEntity = userMapper.toUserEntity(signupReqDTO, hashedPassword, LocalDateTime.now());

        userRepository.save(userEntity);

    }

    // 로그인
    @Transactional
    public SigninResDTO signin(SignupReqDTO signupReqDTO) {

        // 이메일로 사용자 조회
        UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(signupReqDTO.getEmail())
                .orElseThrow(AuthException::emailNotFoundException);

        // 사용자 탈퇴 여부 확인
        if (userEntity.isDeleted()) {
            throw deactivatedAccountException();
        }

        // 소셜 로그인 이력 확인
        if (userEntity.getPlatform() != Platform.OURHOUR) {
            throw AuthException.userAlreadyExistsSocialException();
        }

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signupReqDTO.getPassword(), userEntity.getPassword())) {
            throw invalidPasswordException();
        }

        // JWT token 발급
        String accessToken = jwtTokenProvider.generateAccessToken(authServiceHelper.createClaims(userEntity));
        String refreshToken = jwtTokenProvider.generateRefreshToken(authServiceHelper.createClaims(userEntity));

        // refresh token DB 저장
        authServiceHelper.saveRefreshToken(userEntity, refreshToken);

        // access token 반환
        return new SigninResDTO(accessToken, refreshToken);

    }

    // access token 재발급
    @Transactional
    public SigninResDTO reissueAccessToken(String refreshToken) {

        // 사용자로부터 받은 토큰 유효성 검사
        if (refreshToken == null || refreshToken.isBlank()) {
            throw tokenNotFoundException();
        }

        // DB 조회 및 유효성 검사
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(AuthException::tokenNotFoundException);

        // refresh token 만료일 확인
        if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw invalidTokenException();
        }

        UserEntity userEntity = refreshTokenEntity.getUserEntity();
        if (userEntity == null) {
            throw userNotFoundException();
        }

        // access token 재발급
        String accessToken = jwtTokenProvider.generateAccessToken(authServiceHelper.createClaims(userEntity));

        // access token 반환
        return new SigninResDTO(accessToken, null);

    }

    @Transactional
    public void signout() {

        // 인증 정보 확인
        Long userId = SecurityUtil.getCurrentUserId();
        if(userId == null) {
            throw AuthException.unauthorizedException();
        }

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByUserEntity_UserId(userId);
        if (refreshTokenEntity != null) {
            refreshTokenRepository.delete(refreshTokenEntity);
        }

    }

}
