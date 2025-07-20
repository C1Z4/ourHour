package com.ourhour.domain.project.aspect;

import java.util.List;

import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.exception.BusinessException;
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
    public void projectParticipantOnly() {}

    @Before("projectParticipantOnly() && args(projectId,..)")
    public void checkProjectParticipant(JoinPoint joinPoint, Long projectId) {
        validateProjectId(projectId);
        
        Long orgId = getOrgIdByProject(projectId);
        
        validateProjectAccess(orgId);
    }

    private void validateProjectId(Long projectId) {
        if (projectId == null) {
            throw BusinessException.badRequest("프로젝트 ID가 필요합니다.");
        }
    }

    private Long getOrgIdByProject(Long projectId) {
        Long orgId = projectRepository.findOrgIdByProjectId(projectId);
        if (orgId == null) {
            throw BusinessException.notFound("프로젝트를 찾을 수 없습니다.");
        }
        return orgId;
    }

    private void validateProjectAccess(Long orgId) {
        Claims claims = UserContextHolder.get();
        
        if (claims == null) {
            throw BusinessException.unauthorized("인증 정보가 없습니다.");
        }
        
        List<OrgAuthority> orgAuthorities = claims.getOrgAuthorityList();
        
        boolean hasPermission = orgAuthorities.stream()
                .anyMatch(orgAuthority -> orgAuthority.getOrgId().equals(orgId));
        
        if (!hasPermission) {
            throw BusinessException.forbidden("프로젝트 참여자만 접근할 수 있습니다.");
        }
    }
}
