package com.ourhour.domain.project.aspect;

import java.util.List;

import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.project.annotation.ProjectId;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.jwt.dto.OrgAuthority;

import com.ourhour.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ProjectParticipantAspect {

    private final ProjectRepository projectRepository;

    @Pointcut("@annotation(com.ourhour.domain.project.annotation.ProjectParticipantOnly)")
    public void projectParticipantOnly() {
    }

    @Before("projectParticipantOnly()")
    public void checkProjectParticipant(JoinPoint joinPoint) {
        Long projectId = extractProjectIdFromArgs(joinPoint);

        validateProjectId(projectId);

        Long orgId = getOrgIdByProject(projectId);

        validateProjectAccess(orgId, projectId);
    }

    private Long extractProjectIdFromArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof ProjectId && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }
        }

        throw ProjectException.projectIdRequiredException();
    }

    private void validateProjectId(Long projectId) {
        if (projectId == null) {
            throw ProjectException.projectIdRequiredException();
        }
    }

    private Long getOrgIdByProject(Long projectId) {
        Long orgId = projectRepository.findOrgIdByProjectId(projectId);
        
        if (orgId == null) {
            throw ProjectException.projectNotFoundException();
        }
        return orgId;
    }

    private void validateProjectAccess(Long orgId, Long projectId) {
        CustomUserDetails currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw AuthException.unauthorizedException();
        }

        // 먼저 조직 권한 확인
        List<OrgAuthority> orgAuthorities = currentUser.getOrgAuthorityList();
        boolean hasOrgPermission = orgAuthorities.stream()
                .anyMatch(orgAuthority -> orgAuthority.getOrgId().equals(orgId));

        if (!hasOrgPermission) {
            throw ProjectException.projectParticipantRequiredException();
        }

        // 실제 프로젝트 참여자인지 확인
        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw ProjectException.projectParticipantRequiredException();
        }

        boolean isProjectParticipant = projectRepository.existsByProjectIdAndMemberId(projectId, memberId);

        if (!isProjectParticipant) {
            throw ProjectException.projectParticipantRequiredException();
        }
    }
}
