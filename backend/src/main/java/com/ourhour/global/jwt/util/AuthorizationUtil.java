package com.ourhour.global.jwt.util;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.dto.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtil {

    // true: 사용자가 가지고 있는 회사 아이디 중 하나라도 일치하는 값이 있다면 그 회사에 소속되어 있는 것
    public static boolean isMemberOfOrg(Claims claims, Long orgId) {

        return claims.getOrgAuthorityList().stream()
                .anyMatch(orgAuthority -> orgAuthority.getOrgId().equals(orgId));
    }

    // true: 해당 조직에 속해있고, 필요 권한 이상의 권한을 가지고 있는 것
    public static boolean isHigherThan(Claims claims, Long orgId ,Role accessLevel) {

        return claims.getOrgAuthorityList().stream()
                .filter(orgAuthority -> orgAuthority.getOrgId().equals(orgId))
                .anyMatch(orgAuthority -> orgAuthority.getRole().isHigherThan(accessLevel));
    }
}
