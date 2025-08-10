package com.ourhour.domain.project.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHMilestoneState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.enums.IssueStatus;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;
import com.ourhour.domain.user.entity.GitHubTokenEntity;
import com.ourhour.domain.user.repository.GitHubTokenRepository;
import com.ourhour.domain.user.dto.GitHubRepositoryResDTO;
import com.ourhour.domain.user.dto.GitHubTokenReqDTO;
import com.ourhour.domain.user.dto.GitHubSyncTokenDTO;
import com.ourhour.domain.project.dto.SyncStatusDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.UserContextHolder;
import com.ourhour.global.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubIntegrationService {

    private final GitHubTokenRepository gitHubTokenRepository;
    private final ProjectGithubIntegrationRepository projectGithubIntegrationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final MilestoneRepository milestoneRepository;
    private final EncryptionUtil encryptionUtil;

    // GitHub 토큰으로 레포지토리 이름 목록 조회
    public ApiResponse<List<GitHubRepositoryResDTO>> getGitHubRepositories(GitHubTokenReqDTO token) {
        try {
            // GitHub 클라이언트 생성
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(token.getToken())
                    .build();

            // 토큰 유효성 검증
            try {
                gitHub.getMyself().getLogin();
            } catch (IOException e) {
                log.error("GitHub 토큰 인증 실패", e);
                throw GithubException.githubTokenNotAuthorizedException();
            }

            // 사용자가 접근 가능한 모든 레포지토리 조회
            List<GitHubRepositoryResDTO> repositories = gitHub.getMyself().getRepositories().values().stream()
                    .map(this::convertToRepositoryDTO)
                    .collect(Collectors.toList());

            return ApiResponse.success(repositories, "GitHub 레포지토리 목록 조회에 성공했습니다.");

        } catch (IOException e) {
            log.error("GitHub 레포지토리 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

    // GHRepository를 DTO로 변환
    private GitHubRepositoryResDTO convertToRepositoryDTO(GHRepository repository) {
        return GitHubRepositoryResDTO.builder()
                .id(repository.getId())
                .fullName(repository.getFullName())
                .build();
    }

    // GitHub에서 이슈를 우리 서비스로 동기화
    @Transactional
    public ApiResponse<Void> syncIssuesFromGitHub(Long projectId) {
        try {
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(integration.getGithubAccessToken())
                    .build();

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            // PagedIterable 사용 - 자동 페이징 처리
            PagedIterable<GHIssue> githubIssues = repository.listIssues(GHIssueState.ALL);

            int processedCount = 0;
            int batchSize = 50; // 배치 단위로 처리

            List<IssueEntity> batchIssues = new ArrayList<>();

            for (GHIssue githubIssue : githubIssues) {
                try {
                    IssueEntity issueEntity = processGithubIssue(githubIssue, projectId);
                    if (issueEntity != null) {
                        batchIssues.add(issueEntity);
                    }

                    // 배치 단위로 저장
                    if (batchIssues.size() >= batchSize) {
                        issueRepository.saveAll(batchIssues);
                        batchIssues.clear();
                        log.info("배치 저장 완료 - 처리된 이슈 수: {}", processedCount + batchSize);
                    }

                    processedCount++;

                } catch (Exception e) {
                    log.error("이슈 동기화 중 오류 발생 - Issue #{}", githubIssue.getNumber(), e);
                }
            }

            // 남은 이슈들 저장
            if (!batchIssues.isEmpty()) {
                issueRepository.saveAll(batchIssues);
            }

            log.info("GitHub 이슈 동기화 완료 - 프로젝트 ID: {}, 처리된 이슈 수: {}", projectId, processedCount);
            return ApiResponse.success(null, "GitHub 이슈 동기화가 완료되었습니다.");

        } catch (IOException e) {
            log.error("GitHub 이슈 동기화 실패 - 프로젝트 ID: {}", projectId, e);
            throw GithubException.githubSyncFailedException();
        }
    }

    // GitHub 이슈 동기화 처리
    private IssueEntity processGithubIssue(GHIssue githubIssue, Long projectId) throws IOException {
        // GitHub ID로 기존 이슈 찾기
        Optional<IssueEntity> existingIssue = issueRepository.findByProjectEntity_ProjectIdAndGithubId(
                projectId, (long) githubIssue.getNumber());

        if (existingIssue.isPresent()) {
            // 기존 이슈 업데이트
            IssueEntity issue = existingIssue.get();
            issue.setName(githubIssue.getTitle());
            issue.setContent(githubIssue.getBody());
            issue.setStatus(
                    githubIssue.getState() == GHIssueState.OPEN ? IssueStatus.IN_PROGRESS : IssueStatus.COMPLETED);
            issue.updateLastSyncTime();
            return issue;
        } else {
            // 새 이슈 생성
            IssueEntity newIssue = IssueEntity.builder()
                    .projectEntity(projectRepository.findById(projectId)
                            .orElseThrow(() -> ProjectException.projectNotFoundException()))
                    .name(githubIssue.getTitle())
                    .content(githubIssue.getBody())
                    .status(githubIssue.getState() == GHIssueState.OPEN ? IssueStatus.IN_PROGRESS
                            : IssueStatus.COMPLETED)
                    .build();
            newIssue.markAsSynced((long) githubIssue.getNumber());
            return newIssue;
        }
    }

    // GitHub에서 마일스톤을 우리 서비스로 동기화
    @Transactional
    public ApiResponse<Void> syncMilestonesFromGitHub(Long projectId) {
        try {
            // 프로젝트 연동 정보 조회
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            // GitHub 클라이언트 생성
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(integration.getGithubAccessToken())
                    .build();

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            // GitHub의 모든 마일스톤 가져오기
            List<GHMilestone> githubMilestones = repository.listMilestones(GHIssueState.ALL).toList();

            for (GHMilestone githubMilestone : githubMilestones) {
                try {
                    // GitHub ID로 기존 마일스톤 찾기
                    Optional<MilestoneEntity> existingMilestone = milestoneRepository
                            .findByProjectEntity_ProjectIdAndGithubId(
                                    projectId, (long) githubMilestone.getNumber());

                    if (existingMilestone.isPresent()) {
                        // 기존 마일스톤 업데이트
                        MilestoneEntity milestone = existingMilestone.get();
                        milestone.setName(githubMilestone.getTitle());
                        milestone.updateLastSyncTime();
                        milestoneRepository.save(milestone);
                    } else {
                        // 새 마일스톤 생성
                        MilestoneEntity newMilestone = MilestoneEntity.builder()
                                .projectEntity(projectRepository.findById(projectId)
                                        .orElseThrow(() -> ProjectException.projectNotFoundException()))
                                .name(githubMilestone.getTitle())
                                .progress((byte) (githubMilestone.getState() == GHMilestoneState.OPEN ? 0 : 100))
                                .build();
                        newMilestone.markAsSynced((long) githubMilestone.getNumber());
                        milestoneRepository.save(newMilestone);
                    }
                } catch (Exception e) {
                    log.error("마일스톤 동기화 중 오류 발생 - Milestone #{}", githubMilestone.getNumber(), e);
                }
            }

            log.info("GitHub 마일스톤 동기화 완료 - 프로젝트 ID: {}, 마일스톤 수: {}", projectId, githubMilestones.size());

            return ApiResponse.success(null, "GitHub 마일스톤 동기화가 완료되었습니다.");

        } catch (IOException e) {
            log.error("GitHub 마일스톤 동기화 실패 - 프로젝트 ID: {}", projectId, e);
            throw GithubException.githubSyncFailedException();
        }
    }

    // GitHub에서 모든 데이터 동기화
    @Transactional
    public ApiResponse<Void> syncAllFromGitHub(Long projectId) {
        try {
            // 이슈와 마일스톤 모두 동기화
            syncIssuesFromGitHub(projectId);
            syncMilestonesFromGitHub(projectId);

            return ApiResponse.success(null, "GitHub 전체 동기화가 완료되었습니다.");

        } catch (Exception e) {
            log.error("GitHub 전체 동기화 실패 - 프로젝트 ID: {}", projectId, e);
            throw GithubException.githubSyncFailedException();
        }
    }

    // 동기화 상태 조회
    public ApiResponse<SyncStatusDTO> getSyncStatus(Long projectId) {
        try {
            // 프로젝트 연동 정보 조회
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            // 동기화 상태 정보 수집
            List<IssueEntity> issues = issueRepository.findByProjectEntity_ProjectId(projectId);
            List<MilestoneEntity> milestones = milestoneRepository.findByProjectEntity_ProjectId(projectId);

            SyncStatusDTO syncStatus = SyncStatusDTO.builder()
                    .lastSyncedAt(integration.getLastSyncedAt())
                    .syncStatus(integration.getSyncStatus())
                    .syncedIssues(issues.stream().filter(IssueEntity::getIsGithubSynced).count())
                    .totalIssues((long) issues.size())
                    .syncedMilestones(milestones.stream().filter(MilestoneEntity::getIsGithubSynced).count())
                    .totalMilestones((long) milestones.size())
                    .build();

            return ApiResponse.success(syncStatus, "동기화 상태 조회에 성공했습니다.");

        } catch (Exception e) {
            log.error("동기화 상태 조회 실패 - 프로젝트 ID: {}", projectId, e);
            throw GithubException.githubSyncFailedException();
        }
    }

    // 프로젝트에 GitHub 연동 설정
    @Transactional
    public ApiResponse<Void> connectProjectToGitHub(Long projectId, GitHubSyncTokenDTO gitHubSyncTokenDTO,
            Long memberId) {

        // 토큰 유효성 및 레포지토리 접근 권한 검증
        try {
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(gitHubSyncTokenDTO.getGithubAccessToken())
                    .build();

            // 실제로 레포지토리에 접근 가능한지 테스트
            GHRepository testRepo = gitHub.getRepository(gitHubSyncTokenDTO.getGithubRepository());
            testRepo.getFullName(); // 권한 확인

        } catch (IOException e) {
            log.error("GitHub 레포지토리 접근 권한 없음: {}", gitHubSyncTokenDTO.getGithubRepository(), e);
            throw GithubException.githubRepositoryAccessDeniedException();
        }

        // 기존 연동 정보가 있으면 업데이트, 없으면 생성
        ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                .findByProjectEntity_ProjectIdAndMemberEntity_MemberId(projectId, memberId)
                .orElse(ProjectGithubIntegrationEntity.builder()
                        .projectEntity(projectRepository.findById(projectId)
                                .orElseThrow(() -> ProjectException.projectNotFoundException()))
                        .memberEntity(memberRepository.findById(memberId)
                                .orElseThrow(() -> MemberException.memberNotFoundException()))
                        .githubRepository(gitHubSyncTokenDTO.getGithubRepository())
                        .githubAccessToken(encryptionUtil.encrypt(gitHubSyncTokenDTO.getGithubAccessToken()))
                        .build());

        if (integration.getIsActive()) {
            throw GithubException.githubRepositoryAlreadyConnectedException();
        }

        if (integration.getGithubId() != null) {
            integration.updateRepository(gitHubSyncTokenDTO.getGithubRepository());
            integration.markAsSynced(integration.getGithubId());
        }

        projectGithubIntegrationRepository.save(integration);
        return ApiResponse.success(null, "GitHub 연동이 완료되었습니다.");
    }

    // GitHub 레포지토리 마일스톤 목록 조회
    public ApiResponse<PageResponse<GHMilestone>> getGitHubRepositoryMilestones(String repositoryName, Long memberId,
            int currentPage, int size) {
        try {
            Claims claims = UserContextHolder.get();
            if (claims == null) {
                throw AuthException.unauthorizedException();
            }

            // 사용자의 GitHub 토큰 조회
            GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(claims.getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());

            String decrypted = tokenEntity.getGithubAccessToken();
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(decrypted)
                    .build();

            GHRepository repository = gitHub.getRepository(repositoryName);

            // GitHub API 페이징 사용
            PagedIterable<GHMilestone> pagedIterable = repository.listMilestones(GHIssueState.ALL);

            // 전체 개수 조회 (별도 API 호출)
            int totalCount = pagedIterable.toList().size();

            // 현재 페이지 데이터 조회
            List<GHMilestone> milestones = pagedIterable.toList();
            int startIndex = (currentPage - 1) * size;
            int endIndex = Math.min(startIndex + size, milestones.size());
            List<GHMilestone> pagedMilestones = milestones.subList(startIndex, endIndex);

            // PageResponse 생성
            PageResponse<GHMilestone> pageResponse = PageResponse.<GHMilestone>builder()
                    .data(pagedMilestones)
                    .currentPage(currentPage)
                    .size(size)
                    .totalElements(totalCount)
                    .totalPages((int) Math.ceil((double) totalCount / size))
                    .build();

            return ApiResponse.success(pageResponse, "GitHub 마일스톤 목록 조회에 성공했습니다.");
        } catch (IOException e) {
            log.error("GitHub 마일스톤 목록 조회 중 오류 발생", e);
            throw GithubException.githubMilestoneListNotFoundException();
        }
    }

    // GitHub 레포지토리 마일스톤별 이슈 목록 조회
    public ApiResponse<PageResponse<GHIssue>> getGitHubRepositoryIssues(String repositoryName, int milestoneNumber,
            Long memberId, int currentPage, int size) {
        try {
            Claims claims = UserContextHolder.get();
            if (claims == null) {
                throw AuthException.unauthorizedException();
            }

            // 사용자의 GitHub 토큰 조회
            GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(claims.getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());

            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(tokenEntity.getGithubAccessToken())
                    .build();

            GHRepository repository = gitHub.getRepository(repositoryName);
            GHMilestone milestone = repository.getMilestone(milestoneNumber);
            List<GHIssue> issues = repository.getIssues(GHIssueState.ALL, milestone);

            int totalCount = issues.size();
            int startIndex = (currentPage - 1) * size;
            int endIndex = Math.min(startIndex + size, issues.size());
            List<GHIssue> pagedIssues = issues.subList(startIndex, endIndex);

            PageResponse<GHIssue> pageResponse = PageResponse.<GHIssue>builder()
                    .data(pagedIssues)
                    .currentPage(currentPage)
                    .size(size)
                    .totalElements(totalCount)
                    .totalPages((int) Math.ceil((double) totalCount / size))
                    .build();

            return ApiResponse.success(pageResponse, "GitHub 이슈 목록 조회에 성공했습니다.");
        } catch (Exception e) {
            log.error("GitHub 이슈 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

    // GitHub 레포지토리 이슈 댓글 조회
    public ApiResponse<PageResponse<GHIssueComment>> getGitHubRepositoryIssueComments(String repositoryName,
            int issueNumber, Long memberId, int currentPage, int size) {
        try {
            Claims claims = UserContextHolder.get();
            if (claims == null) {
                throw AuthException.unauthorizedException();
            }

            // 사용자의 GitHub 토큰 조회
            GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(claims.getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());

            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(tokenEntity.getGithubAccessToken())
                    .build();

            GHRepository repository = gitHub.getRepository(repositoryName);
            GHIssue issue = repository.getIssue(issueNumber);
            PagedIterable<GHIssueComment> pagedIterable = issue.listComments();

            int totalCount = pagedIterable.toList().size();
            List<GHIssueComment> comments = pagedIterable.toList();
            int startIndex = (currentPage - 1) * size;
            int endIndex = Math.min(startIndex + size, comments.size());
            List<GHIssueComment> pagedComments = comments.subList(startIndex, endIndex);

            PageResponse<GHIssueComment> pageResponse = PageResponse.<GHIssueComment>builder()
                    .data(pagedComments)
                    .currentPage(currentPage)
                    .size(size)
                    .totalElements(totalCount)
                    .totalPages((int) Math.ceil((double) totalCount / size))
                    .build();

            return ApiResponse.success(pageResponse, "GitHub 댓글 목록 조회에 성공했습니다.");
        } catch (IOException e) {
            log.error("GitHub 댓글 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

    // (연동)프로젝트별 이슈 조회
    public ApiResponse<List<GHIssue>> getProjectIssues(Long projectId) {
        try {
            Claims claims = UserContextHolder.get();
            if (claims == null) {
                throw AuthException.unauthorizedException();
            }

            // 프로젝트 연동 정보에서 토큰도 함께 조회
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            // 프로젝트에 저장된 토큰 사용
            String decrypted = integration.getGithubAccessToken();
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(decrypted) // 프로젝트별 토큰
                    .build();

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            List<GHIssue> issues = repository.getIssues(GHIssueState.ALL);

            return ApiResponse.success(issues, "GitHub 이슈 목록 조회에 성공했습니다.");
        } catch (IOException e) {
            log.error("GitHub 이슈 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

    // (연동)프로젝트별 마일스톤 조회
    public ApiResponse<List<GHMilestone>> getProjectMilestones(Long projectId) {
        try {
            Claims claims = UserContextHolder.get();
            if (claims == null) {
                throw AuthException.unauthorizedException();
            }

            // 프로젝트 연동 정보에서 토큰도 함께 조회
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            // 프로젝트에 저장된 토큰 사용
            String decrypted = integration.getGithubAccessToken();
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(decrypted) // 프로젝트별 토큰
                    .build();

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            List<GHMilestone> milestones = repository.listMilestones(GHIssueState.ALL).toList();

            return ApiResponse.success(milestones, "GitHub 마일스톤 목록 조회에 성공했습니다.");
        } catch (IOException e) {
            log.error("GitHub 마일스톤 목록 조회 중 오류 발생", e);
            throw GithubException.githubMilestoneListNotFoundException();
        }
    }

    // 깃허브 레포지토리 형식 검증
    private boolean isValidGitHubRepositoryFormat(String repository) {
        if (repository == null || repository.trim().isEmpty()) {
            return false;
        }

        // "owner/repo" 형식 검증
        String[] parts = repository.split("/");
        if (parts.length != 2) {
            return false;
        }

        String owner = parts[0];
        String repoName = parts[1];

        // Owner 유효성 검증
        if (!owner.matches("^[a-zA-Z0-9-]+$") || owner.length() > 39 ||
                owner.startsWith("-") || owner.endsWith("-")) {
            return false;
        }

        // Repository 이름 유효성 검증
        if (!repoName.matches("^[a-zA-Z0-9._-]+$") || repoName.length() > 100 ||
                repoName.startsWith("-") || repoName.endsWith("-") || repoName.contains("--")) {
            return false;
        }

        return true;
    }
}