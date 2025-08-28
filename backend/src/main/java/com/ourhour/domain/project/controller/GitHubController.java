package com.ourhour.domain.project.controller;

import com.ourhour.domain.project.service.GithubIntegrationService;
import com.ourhour.domain.user.dto.GitHubTokenReqDTO;
import com.ourhour.domain.user.dto.GitHubSyncTokenDTO;
import com.ourhour.domain.user.dto.GitHubRepositoryResDTO;
import com.ourhour.domain.user.dto.GitHubRepositoryConnectDTO;
import com.ourhour.domain.project.dto.SyncStatusDTO;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.comment.dto.CommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Tag(name = "깃허브 연동", description = "프로젝트와 GitHub 데이터 동기화/조회 API")
public class GitHubController {

    private final GithubIntegrationService githubIntegrationService;

    // 개인 GitHub 토큰으로 레포지토리 목록 조회
    @GetMapping("/user/repositories")
    @Operation(summary = "개인 토큰으로 레포지토리 목록 조회", description = "로그인한 사용자의 개인 GitHub 토큰으로 레포지토리 목록을 조회합니다.")
    public ApiResponse<List<GitHubRepositoryResDTO>> getUserGitHubRepositories() {
        return githubIntegrationService.getUserGitHubRepositories();
    }

    // 프로젝트별 GitHub 연동(개인 토큰 기반)
    @PostMapping("/projects/{projectId}/connect")
    @Operation(summary = "프로젝트 깃허브 연동", description = "개인 GitHub 토큰으로 프로젝트 연동을 설정합니다.")
    public ResponseEntity<ApiResponse<Void>> connectProjectToGitHub(
            @PathVariable Long projectId,
            @RequestBody @Valid GitHubRepositoryConnectDTO connectDTO,
            @RequestParam Long memberId) {
        return ResponseEntity.ok(
                githubIntegrationService.connectProjectToGitHub(projectId, connectDTO, memberId));
    }

    // 프로젝트별 GitHub 연동 해제
    @DeleteMapping("/projects/{projectId}/disconnect")
    @Operation(summary = "프로젝트 깃허브 연동 해제", description = "프로젝트와 GitHub 연동을 해제합니다.")
    public ResponseEntity<ApiResponse<Void>> disconnectProjectFromGitHub(@PathVariable Long projectId) {
        return ResponseEntity.ok(githubIntegrationService.disconnectProjectFromGitHub(projectId));
    }

    // GitHub에서 모든 데이터 동기화 (마일스톤, 이슈, 이슈 댓글 불러오기)
    @PostMapping("/projects/{projectId}/sync/all")
    @Operation(summary = "전체 데이터 동기화", description = "GitHub의 마일스톤/이슈/댓글을 모두 동기화합니다.")
    public ResponseEntity<ApiResponse<Void>> syncAllFromGitHub(@PathVariable Long projectId) {
        return ResponseEntity.ok(githubIntegrationService.syncAllFromGitHub(projectId));
    }

    // 동기화 상태 조회
    @GetMapping("/projects/{projectId}/sync/status")
    @Operation(summary = "동기화 상태 조회", description = "프로젝트의 GitHub 동기화 진행 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<SyncStatusDTO>> getSyncStatus(@PathVariable Long projectId) {
        return ResponseEntity.ok(githubIntegrationService.getSyncStatus(projectId));
    }

    // 깃허브 레포지토리 마일스톤 목록 조회
    @GetMapping("/projects/repositories/{owner}/{repo}/milestones")
    @Operation(summary = "레포지토리 마일스톤 목록 조회", description = "소유자/레포지토리 기준으로 마일스톤 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<MileStoneInfoDTO>>> getGitHubRepositoryMilestones(
            @PathVariable String owner, @PathVariable String repo, @RequestParam Long memberId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {
        String repositoryName = owner + "/" + repo;
        return ResponseEntity.ok(githubIntegrationService.getGitHubRepositoryMilestones(repositoryName, memberId,
                currentPage, size));
    }

    // 깃허브 레포지토리 마일스톤 별 이슈 목록 조회
    @GetMapping("/projects/repositories/{owner}/{repo}/milestones/{milestoneNumber}/issues")
    @Operation(summary = "마일스톤 이슈 목록 조회", description = "특정 마일스톤 번호에 해당하는 이슈 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<IssueDetailDTO>>> getGitHubRepositoryIssues(
            @PathVariable String owner, @PathVariable String repo,
            @PathVariable int milestoneNumber, @RequestParam Long memberId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {
        String repositoryName = owner + "/" + repo;
        return ResponseEntity
                .ok(githubIntegrationService.getGitHubRepositoryIssues(repositoryName, milestoneNumber, memberId,
                        currentPage, size));
    }

    // 깃허브 레포지토리 이슈 댓글 목록 조회
    @GetMapping("/projects/repositories/{owner}/{repo}/issues/{issueNumber}/comments")
    @Operation(summary = "이슈 댓글 목록 조회", description = "특정 이슈 번호의 댓글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<CommentDTO>>> getGitHubRepositoryIssueComments(
            @PathVariable String owner, @PathVariable String repo,
            @PathVariable int issueNumber, @RequestParam Long memberId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {
        String repositoryName = owner + "/" + repo;
        return ResponseEntity
                .ok(githubIntegrationService.getGitHubRepositoryIssueComments(repositoryName, issueNumber, memberId,
                        currentPage, size));
    }
}
