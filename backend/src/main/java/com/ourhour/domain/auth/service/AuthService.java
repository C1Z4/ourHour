package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.SigninResDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.entity.RefreshTokenEntity;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.auth.repository.RefreshTokenRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.mapper.UserMapper;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.mapper.JwtClaimMapper;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ourhour.domain.auth.exception.AuthException.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtClaimMapper jwtClaimMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
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
        String hashedPassword = passwordEncoder.encode(signupReqDTO.getPassword());
        UserEntity userEntity = userMapper.toUserEntity(signupReqDTO, hashedPassword , LocalDateTime.now());

        userRepository.save(userEntity);

    }

    // 로그인
    @Transactional
    public SigninResDTO signin (SignupReqDTO signupReqDTO) {

        // 이메일로 사용자 조회
        UserEntity userEntity = userRepository.findByEmail(signupReqDTO.getEmail())
                .orElseThrow(AuthException::emailNotFoundException);

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signupReqDTO.getPassword(), userEntity.getPassword())) {
            throw invalidPasswordException();
        }

        // JWT token 발급
        String accessToken = jwtTokenProvider.generateAccessToken(createClaims(userEntity));
        String refreshToken = jwtTokenProvider.generateRefreshToken(createClaims(userEntity));

        // refresh token  DB 저장
        saveRefreshToken(userEntity, refreshToken);

        // access token 반환
        return new SigninResDTO(accessToken, refreshToken);

    }

    // Claim 생성
    private Claims createClaims(UserEntity userEntity) {

        // UserEntity와 연결된 모든 MemberEntity 조회
        List<MemberEntity> memberEntityList = userEntity.getMemberEntityList();

        // MemberEntity에 연결된 OrgParticipantMemberEntity 조회
        List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();
        for (MemberEntity memberEntity : memberEntityList) {
            OrgParticipantMemberEntity orgParticipantMemberEntity = memberEntity.getOrgParticipantMemberEntity();
            orgParticipantMemberEntityList.add(orgParticipantMemberEntity);
        }

        return jwtClaimMapper.createClaim(userEntity, orgParticipantMemberEntityList);

    }

    // refresh token 저장
    private void saveRefreshToken(UserEntity userEntity, String refreshToken) {

        // 기존 refresh token 있다면 삭제
        refreshTokenRepository.findByUserEntity(userEntity).ifPresent(refreshTokenRepository::delete);

        // refresh token  DB 저장
        LocalDateTime createdAt = jwtTokenProvider.getIatFromToken(refreshToken);
        LocalDateTime expiredAt = jwtTokenProvider.getExpFromToken(refreshToken);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .userEntity(userEntity)
                .token(refreshToken)
                .createdAt(createdAt)
                .expiresAt(expiredAt)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

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
        String accessToken = jwtTokenProvider.generateAccessToken(createClaims(userEntity));

        // access token 반환
        return new SigninResDTO(accessToken, null);

    }

    @Transactional
    public void signout() {

        // 인증 정보 확인
        Claims claims = UserContextHolder.get();
        if (claims == null) {
            throw unauthorizedException();
        }

        // db에서 refresh token 삭제
        Long userId = claims.getUserId();
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByUserEntity_UserId(userId);
        if (refreshTokenEntity != null) {
            refreshTokenRepository.delete(refreshTokenEntity);
        }

    }

}
