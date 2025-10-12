package com.ourhour.domain.project.service;

import org.springframework.stereotype.Service;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.global.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 관련 권한 검사를 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final ProjectParticipantService projectParticipantService;

    /**
     * 프로젝트 참여자 또는 관리자 권한 검증
     */
    public void validateProjectParticipantOrAdmin(Long orgId, Long projectId) {
        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw MemberException.memberAccessDeniedException();
        }

        boolean isParticipant = projectParticipantService.isProjectParticipant(projectId, memberId);
        Role role = SecurityUtil.getCurrentRoleByOrgId(orgId);
        boolean isAdminOrRootAdmin = role != null && (role.equals(Role.ADMIN) || role.equals(Role.ROOT_ADMIN));

        if (!(isParticipant || isAdminOrRootAdmin)) {
            throw ProjectException.projectParticipantOrAdminOrRootAdminException();
        }
    }

    /**
     * 관리자 권한 검증
     */
    public void validateAdminRole(Long orgId) {
        Role role = SecurityUtil.getCurrentRoleByOrgId(orgId);
        if (role == null || (!role.equals(Role.ADMIN) && !role.equals(Role.ROOT_ADMIN))) {
            throw MemberException.memberAccessDeniedException();
        }
    }

    /**
     * 현재 사용자의 멤버 ID 조회
     */
    public Long getCurrentMemberId(Long orgId) {
        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw MemberException.memberAccessDeniedException();
        }
        return memberId;
    }
}
