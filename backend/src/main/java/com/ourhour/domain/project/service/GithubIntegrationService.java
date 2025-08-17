package com.ourhour.domain.project.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.util.SecurityUtil;
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
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.comment.dto.CommentDTO;
import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.global.util.EncryptionUtil;
import com.ourhour.domain.project.github.GitHubClientFactory;
import com.ourhour.domain.project.github.GitHubDtoMapper;
import com.ourhour.global.util.PaginationUtil;
import com.ourhour.domain.user.repository.UserGitHubMappingRepository;

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
    private final GitHubClientFactory gitHubClientFactory;
    private final GitHubDtoMapper gitHubDtoMapper;
    private final UserGitHubMappingRepository userGitHubMappingRepository;
    private final CommentRepository commentRepository;

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
                    .map(gitHubDtoMapper::toRepository)
                    .collect(Collectors.toList());

            return ApiResponse.success(repositories, "GitHub 레포지토리 목록 조회에 성공했습니다.");

        } catch (IOException e) {
            log.error("GitHub 레포지토리 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

    // GitHub에서 이슈를 우리 서비스로 동기화
    @Transactional
    public ApiResponse<Void> syncIssuesFromGitHub(Long projectId) {
        try {
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            GitHub gitHub = gitHubClientFactory.forEncryptedToken(integration.getGithubAccessToken());

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            // PagedIterable 사용 - 자동 페이징 처리
            PagedIterable<GHIssue> githubIssues = repository.listIssues(GHIssueState.OPEN);

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

        // GitHub 마일스톤 매핑
        MilestoneEntity mappedMilestone = null;
        if (githubIssue.getMilestone() != null) {
            int milestoneNumber = githubIssue.getMilestone().getNumber();
            mappedMilestone = milestoneRepository
                    .findByProjectEntity_ProjectIdAndGithubId(projectId, (long) milestoneNumber)
                    .orElse(null);
        }

        if (existingIssue.isPresent()) {
            // 기존 이슈 업데이트
            IssueEntity issue = existingIssue.get();
            issue.setName(githubIssue.getTitle());
            issue.setContent(githubIssue.getBody());
            issue.setStatus(
                    githubIssue.getState() == GHIssueState.OPEN ? IssueStatus.IN_PROGRESS : IssueStatus.COMPLETED);
            // 마일스톤 매핑 갱신
            issue.setMilestoneEntity(mappedMilestone);
            issue.updateLastSyncTime();
            return issue;
        } else {
            // 새 이슈 생성
            IssueEntity newIssue = IssueEntity.builder()
                    .projectEntity(projectRepository.findById(projectId)
                            .orElseThrow(() -> ProjectException.projectNotFoundException()))
                    .milestoneEntity(mappedMilestone)
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

            // GitHub 클라이언트 생성 (암호화 토큰 복호화)
            GitHub gitHub = gitHubClientFactory.forEncryptedToken(integration.getGithubAccessToken());

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

    // GitHub에서 이슈 댓글을 우리 서비스로 동기화
    @Transactional
    public ApiResponse<Void> syncIssueCommentsFromGitHub(Long projectId) {
        try {
            ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                    .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                    .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

            GitHub gitHub = gitHubClientFactory.forEncryptedToken(integration.getGithubAccessToken());

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            // 프로젝트의 모든 이슈를 순회하며 댓글 동기화
            List<IssueEntity> issues = issueRepository.findByProjectEntity_ProjectId(projectId);

            int processed = 0;
            List<CommentEntity> toSaveBatch = new ArrayList<>();
            final int batchSize = 100;

            for (IssueEntity issue : issues) {
                if (issue.getGithubId() == null) {
                    continue; // GitHub와 매핑되지 않은 이슈는 건너뜀
                }

                GHIssue ghIssue = repository.getIssue(issue.getGithubId().intValue());
                PagedIterable<GHIssueComment> comments = ghIssue.listComments();

                for (GHIssueComment ghComment : comments) {
                    try {
                        // 기존 댓글 조회 (이슈ID + GitHub 댓글ID 기준)
                        CommentEntity entity = commentRepository
                                .findByIssueEntity_IssueIdAndGithubId(issue.getId(), ghComment.getId())
                                .orElse(null);

                        if (entity != null) {
                            // 업데이트
                            entity.setContent(ghComment.getBody());
                            entity.updateLastSyncTime();
                        } else {
                            // 신규 생성 (작성자/부모 댓글 매핑)
                            com.ourhour.domain.member.entity.MemberEntity authorEntity = null;
                            try {
                                String ghLogin = ghComment.getUser() != null ? ghComment.getUser().getLogin() : null;
                                if (ghLogin != null) {
                                    var mappingOpt = userGitHubMappingRepository.findByGithubUsername(ghLogin);
                                    if (mappingOpt.isPresent()) {
                                        Long userId = mappingOpt.get().getUserId();
                                        // 동일 조직 내 참여 멤버 매핑
                                        var memberOpt = memberRepository.findMemberInOrgByUserId(
                                                issue.getProjectEntity().getOrgEntity().getOrgId(), userId);
                                        if (memberOpt.isPresent()) {
                                            authorEntity = memberOpt.get();
                                        }
                                    }
                                }
                            } catch (Exception ignore) {
                            }

                            // 매핑 실패 시: 연동을 생성한 멤버를 기본 작성자로 사용
                            if (authorEntity == null) {
                                authorEntity = integration.getMemberEntity();
                            }

                            CommentEntity newComment = CommentEntity.builder()
                                    .issueEntity(issue)
                                    .authorEntity(authorEntity)
                                    .content(ghComment.getBody())
                                    .build();
                            newComment.markAsSynced(ghComment.getId());
                            entity = newComment;
                        }

                        toSaveBatch.add(entity);
                        if (toSaveBatch.size() >= batchSize) {
                            commentRepository.saveAll(toSaveBatch);
                            toSaveBatch.clear();
                        }
                        processed++;
                    } catch (Exception e) {
                        log.error("댓글 동기화 중 오류 - Issue #{}, Comment ID {}", issue.getGithubId(), ghComment.getId(), e);
                    }
                }
            }

            if (!toSaveBatch.isEmpty()) {
                commentRepository.saveAll(toSaveBatch);
            }

            log.info("GitHub 이슈 댓글 동기화 완료 - 프로젝트 ID: {}, 처리된 댓글 수: {}", projectId, processed);
            return ApiResponse.success(null, "GitHub 이슈 댓글 동기화가 완료되었습니다.");

        } catch (IOException e) {
            log.error("GitHub 이슈 댓글 동기화 실패 - 프로젝트 ID: {}", projectId, e);
            throw GithubException.githubSyncFailedException();
        }
    }

    // GitHub에서 모든 데이터 동기화
    @Transactional
    public ApiResponse<Void> syncAllFromGitHub(Long projectId) {
        try {
            // 마일스톤 → 이슈 → 이슈 댓글 순서로 동기화 (이슈-마일스톤 매핑 보장)
            syncMilestonesFromGitHub(projectId);
            syncIssuesFromGitHub(projectId);
            syncIssueCommentsFromGitHub(projectId);

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
                .orElse(null);

        // 동일 레포지토리 연동 여부 확인 (이미 동일 레포 연결 시 토큰만 갱신하고 종료할지, 에러로 할지 정책)
        if (integration != null && integration.getGithubRepository().equals(gitHubSyncTokenDTO.getGithubRepository())) {
            // 동일 레포지토리: 토큰 갱신만 수행
            integration.updateAccessToken(encryptionUtil.encrypt(gitHubSyncTokenDTO.getGithubAccessToken()));
            integration.markAsSynced(integration.getGithubId());
            projectGithubIntegrationRepository.save(integration);
            return ApiResponse.success(null, "GitHub 연동이 갱신되었습니다.");
        }

        // 다른 레포지토리로 연동 업데이트
        if (integration != null) {
            try {
                GitHub gitHub = new GitHubBuilder()
                        .withOAuthToken(gitHubSyncTokenDTO.getGithubAccessToken())
                        .build();

                Long newRepoId = gitHub.getRepository(gitHubSyncTokenDTO.getGithubRepository()).getId();
                integration.setGithubId(newRepoId);
                integration.updateRepository(gitHubSyncTokenDTO.getGithubRepository());
                integration.updateAccessToken(encryptionUtil.encrypt(gitHubSyncTokenDTO.getGithubAccessToken()));
                integration.markAsSynced(newRepoId);

                projectGithubIntegrationRepository.save(integration);
                return ApiResponse.success(null, "GitHub 연동이 업데이트되었습니다.");
            } catch (IOException e) {
                log.error("GitHub 레포지토리 접근 권한 없음: {}", gitHubSyncTokenDTO.getGithubRepository(), e);
                throw GithubException.githubRepositoryAccessDeniedException();
            }
        }

        // 새로운 레포지토리 연동 생성
        ProjectGithubIntegrationEntity newIntegration = ProjectGithubIntegrationEntity.builder()
                .projectEntity(projectRepository.findById(projectId)
                        .orElseThrow(() -> ProjectException.projectNotFoundException()))
                .memberEntity(memberRepository.findById(memberId)
                        .orElseThrow(() -> MemberException.memberNotFoundException()))
                .githubRepository(gitHubSyncTokenDTO.getGithubRepository())
                .githubAccessToken(encryptionUtil.encrypt(gitHubSyncTokenDTO.getGithubAccessToken()))
                .isActive(true)
                .build();

        try {
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(gitHubSyncTokenDTO.getGithubAccessToken())
                    .build();

            Long repoId = gitHub.getRepository(gitHubSyncTokenDTO.getGithubRepository()).getId();
            newIntegration.setGithubId(repoId);
            newIntegration.markAsSynced(repoId);
        } catch (IOException e) {
            log.error("GitHub 레포지토리 접근 권한 없음: {}", gitHubSyncTokenDTO.getGithubRepository(), e);
            throw GithubException.githubRepositoryAccessDeniedException();
        }

        newIntegration.markAsSynced(newIntegration.getGithubId());

        projectGithubIntegrationRepository.save(newIntegration);
        return ApiResponse.success(null, "GitHub 연동이 완료되었습니다.");
    }

    // 프로젝트별 GitHub 연동 해제
    public ApiResponse<Void> disconnectProjectFromGitHub(Long projectId) {

        ProjectGithubIntegrationEntity integration = projectGithubIntegrationRepository
                .findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                .orElseThrow(() -> GithubException.githubRepositoryNotFoundException());

        projectGithubIntegrationRepository.delete(integration);

        return ApiResponse.success(null, "GitHub 연동이 해제되었습니다.");
    }

    // GitHub 레포지토리 마일스톤 목록 조회
    public ApiResponse<PageResponse<MileStoneInfoDTO>> getGitHubRepositoryMilestones(String repositoryName,
            Long memberId,
            int currentPage, int size) {
        try {
            CustomUserDetails currentUser = SecurityUtil.getCurrentUser();
            if (currentUser == null) {
                throw AuthException.unauthorizedException();
            }

            // 사용자의 GitHub 토큰 조회
            GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(currentUser.getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());

            GitHub gitHub = gitHubClientFactory.forEncryptedToken(tokenEntity.getGithubAccessToken());

            GHRepository repository = gitHub.getRepository(repositoryName);

            // GitHub API 페이징 사용
            PagedIterable<GHMilestone> pagedIterable = repository.listMilestones(GHIssueState.ALL);

            List<GHMilestone> all = pagedIterable.toList();
            PageResponse<MileStoneInfoDTO> pageResponse = PaginationUtil.paginate(
                    all, currentPage, size, gitHubDtoMapper::toMilestone);

            return ApiResponse.success(pageResponse, "GitHub 마일스톤 목록 조회에 성공했습니다.");
        } catch (IOException e) {
            log.error("GitHub 마일스톤 목록 조회 중 오류 발생", e);
            throw GithubException.githubMilestoneListNotFoundException();
        }
    }

    // GitHub 레포지토리 마일스톤별 이슈 목록 조회(OPEN 상태만)
    public ApiResponse<PageResponse<IssueDetailDTO>> getGitHubRepositoryIssues(String repositoryName,
            int milestoneNumber,
            Long memberId, int currentPage, int size) {
        try {
            CustomUserDetails currentUser = SecurityUtil.getCurrentUser();
            if (currentUser == null) {
                throw AuthException.unauthorizedException();
            }

            // 사용자의 GitHub 토큰 조회
            GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(currentUser.getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());

            GitHub gitHub = gitHubClientFactory.forEncryptedToken(tokenEntity.getGithubAccessToken());

            GHRepository repository = gitHub.getRepository(repositoryName);
            GHMilestone milestone = repository.getMilestone(milestoneNumber);
            List<GHIssue> issues = repository.getIssues(GHIssueState.OPEN, milestone);

            PageResponse<IssueDetailDTO> pageResponse = PaginationUtil.paginate(
                    issues, currentPage, size, gitHubDtoMapper::toIssue);

            return ApiResponse.success(pageResponse, "GitHub 이슈 목록 조회에 성공했습니다.");
        } catch (Exception e) {
            log.error("GitHub 이슈 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

    // GitHub 레포지토리 이슈 댓글 조회
    public ApiResponse<PageResponse<CommentDTO>> getGitHubRepositoryIssueComments(String repositoryName,
            int issueNumber, Long memberId, int currentPage, int size) {
        try {
            CustomUserDetails currentUser = SecurityUtil.getCurrentUser();
            if (currentUser == null) {
                throw AuthException.unauthorizedException();
            }

            // 사용자의 GitHub 토큰 조회
            GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(currentUser.getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());

            GitHub gitHub = gitHubClientFactory.forEncryptedToken(tokenEntity.getGithubAccessToken());

            GHRepository repository = gitHub.getRepository(repositoryName);
            GHIssue issue = repository.getIssue(issueNumber);
            PagedIterable<GHIssueComment> pagedIterable = issue.listComments();

            PageResponse<CommentDTO> pageResponse = PaginationUtil.paginate(
                    pagedIterable.toList(), currentPage, size, gitHubDtoMapper::toComment);

            return ApiResponse.success(pageResponse, "GitHub 댓글 목록 조회에 성공했습니다.");
        } catch (IOException e) {
            log.error("GitHub 댓글 목록 조회 중 오류 발생", e);
            throw GithubException.githubRepositoryNotFoundException();
        }
    }

}