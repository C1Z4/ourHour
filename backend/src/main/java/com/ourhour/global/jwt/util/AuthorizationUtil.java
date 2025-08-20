package com.ourhour.global.jwt.util;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtil {

    // true: 해당 조직에 속해있고, 필요 권한 이상의 권한을 가지고 있는 것
    public static boolean hasOrgAccess(CustomUserDetails customUserDetails, Long orgId , Role accessLevel) {

        return customUserDetails.getOrgAuthorityList().stream()
                .filter(orgAuthority -> orgAuthority.getOrgId().equals(orgId))
                .anyMatch(orgAuthority -> orgAuthority.getRole().isHigherThan(accessLevel));
    }
}
