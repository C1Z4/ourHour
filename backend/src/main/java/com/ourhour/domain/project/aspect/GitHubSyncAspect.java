package com.ourhour.domain.project.aspect;

import java.util.concurrent.CompletableFuture;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ourhour.domain.project.annotation.GitHubSync;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.sync.GitHubSyncManager;
import com.ourhour.global.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubSyncAspect {

    private final GitHubSyncManager syncManager;
    private final IssueRepository issueRepository;
    private final MilestoneRepository milestoneRepository;

    // 동기화 성공 시 동기화 수행
    @AfterReturning(pointcut = "@annotation(gitHubSync)", returning = "result", argNames = "joinPoint,gitHubSync,result")
    public void syncAfterSuccess(JoinPoint joinPoint, GitHubSync gitHubSync, Object result) {
        try {
            // API 응답이 성공인지 확인
            if (!isSuccessfulApiResponse(result)) {
                return;
            }

            // 동기화할 엔티티 추출
            GitHubSyncableEntity entity = extractEntityFromResult(result, joinPoint, gitHubSync);
            if (entity == null) {
                log.warn("동기화할 엔티티를 찾을 수 없습니다 - Method: {}", joinPoint.getSignature().getName());
                return;
            }

            // 비동기적으로 GitHub 동기화 수행
            CompletableFuture.runAsync(() -> {
                try {
                    syncManager.syncToGitHub(entity, gitHubSync.operation());
                } catch (Exception e) {
                    log.error("비동기 GitHub 동기화 실패", e);
                }
            });

        } catch (Exception e) {
            log.error("GitHub 동기화 AOP 처리 중 오류 발생", e);
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
        // 결과에서 엔티티 추출 시도
        if (result instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) result;
            Object data = apiResponse.getData();

            // DTO에서 엔티티 ID를 추출해서 실제 엔티티를 조회하는 방식
            if (data instanceof IssueDetailDTO) {
                IssueDetailDTO dto = (IssueDetailDTO) data;
                return issueRepository.findById(dto.getIssueId()).orElse(null);
            }

            if (data instanceof MileStoneInfoDTO) {
                MileStoneInfoDTO dto = (MileStoneInfoDTO) data;
                return milestoneRepository.findById(dto.getMilestoneId()).orElse(null);
            }
        }

        // 메서드 파라미터에서 엔티티 추출
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof GitHubSyncableEntity) {
                return (GitHubSyncableEntity) arg;
            }
        }

        return null;
    }
}
