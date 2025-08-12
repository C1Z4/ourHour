package com.ourhour.domain.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.project.annotation.GitHubSync;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.sync.GitHubSyncManager;
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
    private final IssueRepository issueRepository;
    private final MilestoneRepository milestoneRepository;

    // 삭제 작업의 경우, 삭제 전에 로드해 둔 엔티티를 보관하기 위한 ThreadLocal
    private final ThreadLocal<GitHubSyncableEntity> pendingDeleteEntity = new ThreadLocal<>();

    // 삭제 실행 전, 엔티티를 미리 적재해 둔다 (삭제 후에는 조회 불가하기 때문)
    @Before("@annotation(gitHubSync)")
    public void captureEntityBeforeDelete(JoinPoint joinPoint, GitHubSync gitHubSync) {
        try {
            if (gitHubSync.operation() != SyncOperation.DELETE) {
                return;
            }

            // 메서드 인자에서 엔티티 ID를 식별 (entityParam 힌트를 우선 사용)
            Object[] args = joinPoint.getArgs();
            Long idCandidate = null;
            String hintedParam = gitHubSync.entityParam();
            if (hintedParam != null && !hintedParam.isBlank()) {
                var params = ((org.aspectj.lang.reflect.CodeSignature) joinPoint.getSignature()).getParameterNames();
                for (int i = 0; i < params.length && i < args.length; i++) {
                    if (hintedParam.equals(params[i]) && args[i] instanceof Long) {
                        idCandidate = (Long) args[i];
                        break;
                    }
                }
            }
            if (idCandidate == null) {
                for (Object arg : args) {
                    if (arg instanceof Long) {
                        idCandidate = (Long) arg;
                        break;
                    }
                }
            }

            if (idCandidate == null) {
                return;
            }

            GitHubSyncableEntity entity = null;
            // entityParam 힌트를 통해 타입을 우선 판별
            if ("issueId".equals(hintedParam)) {
                entity = issueRepository.findById(idCandidate).orElse(null);
            } else if ("milestoneId".equals(hintedParam)) {
                entity = milestoneRepository.findById(idCandidate).orElse(null);
            } else {
                // 힌트가 없으면 이슈 우선 조회, 없으면 마일스톤 조회
                var issueOpt = issueRepository.findById(idCandidate);
                if (issueOpt.isPresent()) {
                    entity = issueOpt.get();
                } else {
                    entity = milestoneRepository.findById(idCandidate).orElse(null);
                }
            }

            if (entity != null) {
                pendingDeleteEntity.set(entity);
            }
        } catch (Exception ignore) {
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
                extracted = pendingDeleteEntity.get();
            }

            final GitHubSyncableEntity entity = extracted;
            if (entity == null) {
                log.warn("동기화할 엔티티를 찾을 수 없습니다 - Method: {}", joinPoint.getSignature().getName());
                return;
            }
            syncManager.syncToGitHub(entity, gitHubSync.operation());

        } catch (Exception e) {
            log.error("GitHub 동기화 AOP 처리 중 오류 발생", e);
        } finally {
            // ThreadLocal 정리
            pendingDeleteEntity.remove();
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
