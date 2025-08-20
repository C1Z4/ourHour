package com.ourhour.domain.auth.util;

import com.ourhour.domain.auth.entity.RefreshTokenEntity;
import com.ourhour.domain.auth.repository.RefreshTokenRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.mapper.JwtClaimMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthServiceHelper {

    private final JwtClaimMapper jwtClaimMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // Claim 생성
    public Claims createClaims(UserEntity userEntity) {
        // UserEntity와 연결된 모든 MemberEntity 조회
        List<MemberEntity> memberEntityList = userEntity.getMemberEntityList();

        // MemberEntity에 연결된 모든 OrgParticipantMemberEntity 조회
        List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();
        for (MemberEntity memberEntity : memberEntityList) {
            List<OrgParticipantMemberEntity> participantList = memberEntity.getOrgParticipantMemberEntityList();
            orgParticipantMemberEntityList.addAll(participantList);
        }

        return jwtClaimMapper.createClaim(userEntity, orgParticipantMemberEntityList);
    }

    // refresh token 저장
    public void saveRefreshToken(UserEntity userEntity, String refreshToken) {

        // 기존 refresh token 있다면 삭제
        refreshTokenRepository.findByUserEntity(userEntity).ifPresent(refreshTokenRepository::delete);

        // refresh token DB 저장
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

    // refresh token 쿠키 세팅
    public void setRefreshTokenCookie(String refreshToken, boolean secure, String samSite, long maxAge, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(samSite)
                .path("/")
                .maxAge(maxAge)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}
