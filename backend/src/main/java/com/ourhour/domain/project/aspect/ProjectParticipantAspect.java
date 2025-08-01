package com.ourhour.domain.project.aspect;

import java.util.List;

import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.OrgAuthority;
import com.ourhour.global.jwt.util.UserContextHolder;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ProjectParticipantAspect {

    private final ProjectRepository projectRepository;

    @Pointcut("@annotation(com.ourhour.domain.project.annotation.ProjectParticipantOnly)")
    public void projectParticipantOnly() {
    }

    @Before("projectParticipantOnly() && args(projectId,..)")
    public void checkProjectParticipant(JoinPoint joinPoint, Long projectId) {
        validateProjectId(projectId);

        Long orgId = getOrgIdByProject(projectId);

        validateProjectAccess(orgId);
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

    private void validateProjectAccess(Long orgId) {
        Claims claims = UserContextHolder.get();

        if (claims == null) {
            throw AuthException.unauthorizedException();
        }

        List<OrgAuthority> orgAuthorities = claims.getOrgAuthorityList();

        boolean hasPermission = orgAuthorities.stream()
                .anyMatch(orgAuthority -> orgAuthority.getOrgId().equals(orgId));

        if (!hasPermission) {
            throw ProjectException.projectParticipantRequiredException();
        }
    }
}
