package com.ourhour.domain.project.aspect.strategy;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import org.aspectj.lang.JoinPoint;

/**
 * GitHubSync 대상 Entity를 추출하기 위한 전략 인터페이스
 */
public interface EntitySyncStrategy {

    /**
     * 이 전략이 처리 가능한 Entity 타입인지 확인
     */
    boolean supports(String entityType);

    /**
     * ID로 Entity 조회
     */
    GitHubSyncableEntity findById(Long id);

    /**
     * DTO 객체로부터 Entity 추출
     */
    GitHubSyncableEntity extractFromDto(Object dto);

    /**
     * JoinPoint의 메서드 파라미터에서 ID 추출
     */
    default Long extractIdFromArgs(JoinPoint joinPoint, String hintedParam) {
        Object[] args = joinPoint.getArgs();

        // 힌트가 제공된 경우, 해당 파라미터명으로 검색
        if (hintedParam != null && !hintedParam.isBlank()) {
            var params = ((org.aspectj.lang.reflect.CodeSignature) joinPoint.getSignature()).getParameterNames();
            for (int i = 0; i < params.length && i < args.length; i++) {
                if (hintedParam.equals(params[i]) && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }
        }

        // 힌트가 없거나 찾지 못한 경우, 첫 번째 Long 타입 파라미터 반환
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }

        return null;
    }
}
