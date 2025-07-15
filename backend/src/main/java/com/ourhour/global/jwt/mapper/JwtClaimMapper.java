package com.ourhour.global.jwt.mapper;

import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.global.jwt.dto.OrgAuthority;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtClaimMapper {

    @Autowired
    private OrgAuthorityMapper orgAuthorityMapper;

    // Jwt 파싱 -> Claim 객체 추출
    public io.jsonwebtoken.Claims getJwtClaims(SecretKey secretKey, String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Entity -> Custom Claims DTO 생성
    public com.ourhour.global.jwt.dto.Claims createClaim(UserEntity userEntity, List<OrgParticipantMemberEntity> orgMembers) {

        List<OrgAuthority> authorityList = orgAuthorityMapper.toOrgAuthority(orgMembers);

        if(authorityList == null) {
            authorityList = new ArrayList<>(); 
        }

        return com.ourhour.global.jwt.dto.Claims.builder()
                .email(userEntity.getEmail())
                .userId(userEntity.getUserId())
                .orgAuthorityList(authorityList)
                .build();
    }
}
