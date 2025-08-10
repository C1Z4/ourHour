package com.ourhour.domain.project.controller;

import com.ourhour.domain.project.service.GithubIntegrationService;
import com.ourhour.domain.user.dto.GitHubTokenReqDTO;
import com.ourhour.domain.user.dto.GitHubSyncTokenDTO;
import com.ourhour.domain.user.dto.GitHubRepositoryResDTO;
import com.ourhour.domain.project.dto.SyncStatusDTO;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import org.kohsuke.github.GHIssueComment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GithubIntegrationService githubIntegrationService;

    // GitHub 토큰으로 레포지토리 목록 조회
    @GetMapping("/token/repositories")
    public ApiResponse<List<GitHubRepositoryResDTO>> getGitHubRepositoriesByToken(
            @RequestBody GitHubTokenReqDTO token) {
        return githubIntegrationService.getGitHubRepositories(token);
    }

    // 프로젝트별 GitHub 연동(기존 프로젝트 연동), 연동 데이터 저장, 연동 데이터 업데이트
    @PostMapping("/projects/{projectId}/connect")
    public ResponseEntity<ApiResponse<Void>> connectProjectToGitHub(
            @PathVariable Long projectId,
            @RequestBody @Valid GitHubSyncTokenDTO gitHubSyncTokenDTO,
            @RequestParam Long memberId) {
        return ResponseEntity
                .ok(githubIntegrationService.connectProjectToGitHub(projectId, gitHubSyncTokenDTO,
                        memberId));
    }

    // GitHub에서 모든 데이터 동기화 (마일스톤, 이슈 불러오기)
    @PostMapping("/projects/{projectId}/sync/all")
    public ResponseEntity<ApiResponse<Void>> syncAllFromGitHub(@PathVariable Long projectId) {
        return ResponseEntity.ok(githubIntegrationService.syncAllFromGitHub(projectId));
    }

    // 동기화 상태 조회
    @GetMapping("/projects/{projectId}/sync/status")
    public ResponseEntity<ApiResponse<SyncStatusDTO>> getSyncStatus(@PathVariable Long projectId) {
        return ResponseEntity.ok(githubIntegrationService.getSyncStatus(projectId));
    }

    // 깃허브 레포지토리 마일스톤 목록 조회
    @GetMapping("/projects/repositories/{owner}/{repo}/milestones")
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
    public ResponseEntity<ApiResponse<PageResponse<GHIssueComment>>> getGitHubRepositoryIssueComments(
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
