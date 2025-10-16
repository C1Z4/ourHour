package com.ourhour.global.util;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.jwt.dto.OrgAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}

    // 현재 인증된 사용자 반환, 인증되지 않았으면 null 반환
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            return null;
        }

        return (CustomUserDetails) principal;
    }

    // 현재 인증된 사용자의 userId 반환, 인증되지 않았으면 null 반환
    public static Long getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        if (user == null) {
            return null;
        }

        return user.getUserId();
    }

    // 특정 orgId에 해당하는 memeberId 반환
    public static Long getCurrentMemberIdByOrgId(Long orgId) {
        CustomUserDetails user = getCurrentUser();
        if (user == null) {
            return null;
        }

        return user.getOrgAuthorityList().stream()
                .filter(orgAuthority -> orgAuthority.getOrgId().equals(orgId))
                .map(OrgAuthority::getMemberId)
                .findFirst()
                .orElse(null);
    }

    // 특정 orgId에 해당하는 role 반환
    public static Role getCurrentRoleByOrgId(Long orgId) {
        CustomUserDetails user = getCurrentUser();
        if (user == null) {
            return null;
        }

        return user.getOrgAuthorityList().stream()
                .filter(orgAuthority -> orgAuthority.getOrgId().equals(orgId))
                .map(OrgAuthority::getRole)
                .findFirst()
                .orElse(null);
    }
}
