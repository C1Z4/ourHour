package com.ourhour.global.jwt.aspect;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.AuthorizationUtil;
import com.ourhour.global.jwt.util.UserContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.nio.file.AccessDeniedException;

@Aspect
@Component
public class JwtOrgAuthorizationAspect {

    @Around("@annotation(orgAuth)")
    public Object authorizeOrgAccess(ProceedingJoinPoint joinPoint, OrgAuth orgAuth) throws Throwable {

        // 현재 인증된 사용자 정보 조회
        Claims currentUserClaims = UserContextHolder.get();

        if (currentUserClaims == null || currentUserClaims.getUserId() == null) {
            throw BusinessException.forbidden("로그인이 필요합니다.");
        }

        // 메소드의 인자에서 @OrgId 어노테이션이 붙은 값을 찾기
        Long orgId = getOrgIdAnnoFromMethod(joinPoint);
        if (orgId == null) {
            throw BusinessException.forbidden("해당 회사의 소속이 아니거나 회사 아이디의 값이 잘못되었습니다.");
        }

        // false: 해당 회사에 대해 가지고 있는 권한이 접근 권한보다 높지 않은 경우
        if (!AuthorizationUtil.isHigherThan(currentUserClaims, orgId, orgAuth.accessLevel())) {
            throw BusinessException.forbidden("접근 권한이 없습니다.");
        }

        // 모든 조건을 통과하면 해당 메소드 실행
        return joinPoint.proceed();

    }

    // 메소드의 인자에서 @OrgId 어노테이션이 붙은 값을 찾는 메소드
    private Long getOrgIdAnnoFromMethod(ProceedingJoinPoint joinPoint) {

        Long orgId = null;
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotation = methodSignature.getMethod().getParameterAnnotations();

        for (int i = 0; i < parameterAnnotation.length; i++) {
            for (Annotation annotation : parameterAnnotation[i]) {
                if (annotation instanceof OrgId) {
                    if (args[i] instanceof Long) {
                        orgId = (Long) args[i];
                        break; // 내부 for 문
                    }
                }
            }
            if (orgId != null) break; // 외부 for 문
        }

        return orgId;

    }

}
