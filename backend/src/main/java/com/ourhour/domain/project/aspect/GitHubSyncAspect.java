package com.ourhour.domain.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.project.annotation.GitHubSync;
import com.ourhour.domain.project.aspect.strategy.EntitySyncStrategyRegistry;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.sync.GitHubSyncManager;
import com.ourhour.domain.project.sync.GitHubSyncContext;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.domain.project.enums.SyncOperation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubSyncAspect {

    private final GitHubSyncManager syncManager;
    private final EntitySyncStrategyRegistry strategyRegistry;

    // 삭제 작업의 경우, 삭제 전에 로드해 둔 엔티티를 보관하기 위한 컨텍스트
    private final ThreadLocal<GitHubSyncContext> syncContext = ThreadLocal.withInitial(GitHubSyncContext::new);

    // 삭제 실행 전, 엔티티를 미리 적재해 둔다 (삭제 후에는 조회 불가하기 때문)
    @Before("@annotation(gitHubSync)")
    public void captureEntityBeforeDelete(JoinPoint joinPoint, GitHubSync gitHubSync) {
        try {
            if (gitHubSync.operation() != SyncOperation.DELETE) {
                return;
            }

            // Strategy Registry를 통해 Entity 조회
            GitHubSyncableEntity entity = strategyRegistry.findEntityFromJoinPoint(
                    joinPoint,
                    gitHubSync.entityParam()
            );

            if (entity != null) {
                syncContext.get().setPendingDeleteEntity(entity);
            }
        } catch (Exception e) {
            log.debug("엔티티 캡처 중 오류 발생: {}", e.getMessage());
        }
    }

    // 동기화 성공 시 동기화 수행
    @AfterReturning(pointcut = "@annotation(gitHubSync)", returning = "result", argNames = "joinPoint,gitHubSync,result")
    @Transactional
    public void syncAfterSuccess(JoinPoint joinPoint, GitHubSync gitHubSync, Object result) {
        try {
            // API 응답이 성공인지 확인
            if (!isSuccessfulApiResponse(result)) {
                return;
            }

            // 동기화할 엔티티 추출
            GitHubSyncableEntity extracted = extractEntityFromResult(result, joinPoint, gitHubSync);

            // 삭제 작업에서 결과로 엔티티를 얻지 못하는 경우, 삭제 이전에 보관해 둔 엔티티 사용
            if (extracted == null && gitHubSync.operation() == SyncOperation.DELETE) {
                extracted = syncContext.get().getPendingDeleteEntity();
            }

            if (extracted == null) {
                log.debug("동기화할 엔티티를 찾을 수 없습니다 - Method: {}", joinPoint.getSignature().getName());
                return;
            }
            syncManager.syncToGitHub(extracted, gitHubSync.operation());

        } catch (Exception e) {
            log.error("GitHub 동기화 AOP 처리 중 오류 발생", e);
        } finally {
            // 컨텍스트 정리
            GitHubSyncContext context = syncContext.get();
            if (context != null) {
                context.clear();
            }
        }
    }

    // API 응답이 성공인지 확인
    private boolean isSuccessfulApiResponse(Object result) {
        if (result instanceof ApiResponse) {
            return ((ApiResponse<?>) result).getStatus().equals(HttpStatus.OK);
        }
        return true; // ApiResponse가 아닌 경우는 성공으로 간주
    }

    // 결과에서 엔티티 추출
    private GitHubSyncableEntity extractEntityFromResult(Object result, JoinPoint joinPoint, GitHubSync gitHubSync) {
        // ApiResponse에서 DTO 추출 후 Strategy Registry로 Entity 변환
        if (result instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) result;
            Object data = apiResponse.getData();

            // Strategy Registry를 통해 DTO에서 Entity 추출
            GitHubSyncableEntity entity = strategyRegistry.extractFromDto(data);
            if (entity != null) {
                return entity;
            }
        }

        // 메서드 파라미터에서 GitHubSyncableEntity 직접 추출
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof GitHubSyncableEntity) {
                return (GitHubSyncableEntity) arg;
            }
        }

        return null;
    }

}
