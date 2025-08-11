package com.ourhour.domain.project.sync;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;
import org.springframework.stereotype.Component;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.enums.IssueStatus;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;
import com.ourhour.domain.user.entity.GitHubTokenEntity;
import com.ourhour.domain.user.entity.UserGitHubMappingEntity;
import com.ourhour.domain.user.repository.GitHubTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IssueSyncHandler implements GitHubSyncHandler<IssueEntity> {

    private final ProjectGithubIntegrationRepository integrationRepository;
    private final GitHubTokenRepository gitHubTokenRepository;
    private final IssueRepository issueRepository; // 동기화 상태 업데이트용

    // 이슈 생성
    @Override
    public void createInGitHub(IssueEntity issue) {
        try {
            ProjectGithubIntegrationEntity integration = getActiveIntegration(issue.getProjectEntity().getProjectId());
            GitHub gitHub = createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            GHIssueBuilder issueBuilder = repository.createIssue(issue.getName());
            if (issue.getContent() != null) {
                issueBuilder.body(issue.getContent());
            }

            // 담당자 설정 (GitHub 사용자명이 있는 경우)
            if (issue.getAssigneeEntity() != null && issue.getAssigneeEntity().getMemberId() != null) {
                UserGitHubMappingEntity githubMapping = issue.getAssigneeEntity().getUserEntity().getGithubMappingEntity();
                if (githubMapping != null) {
                    issueBuilder.assignee(githubMapping.getGithubUsername());
                }
            } else if (issue.getAssigneeEntity() != null) {
                log.warn("GitHub username이 없는 담당자 - Assignee ID: {}", issue.getAssigneeEntity().getMemberId());
                // GitHub username이 없는 경우, assignee 설정 생략
            }

            // 마일스톤 설정 (마일스톤이 GitHub에 동기화된 경우)
            if (issue.getMilestoneEntity() != null && issue.getMilestoneEntity().getGithubId() != null) {
                GHMilestone milestone = repository.getMilestone(issue.getMilestoneEntity().getGithubId().intValue());
                issueBuilder.milestone(milestone);
            }

            GHIssue githubIssue = issueBuilder.create();

            // 이슈 상태가 완료인 경우 GitHub에서도 닫기
            if (issue.getStatus() == IssueStatus.COMPLETED) {
                githubIssue.close();
            }

            // 동기화 정보 업데이트
            issue.markAsSynced((long) githubIssue.getNumber());
            issueRepository.save(issue);

            log.info("GitHub 이슈 생성 완료 - Groupware ID: {}, GitHub Number: {}",
                    issue.getIssueId(), githubIssue.getNumber());

        } catch (IOException e) {
            log.error("GitHub 이슈 생성 실패 - Issue ID: {}", issue.getIssueId(), e);
            throw new RuntimeException("GitHub 이슈 생성 실패", e);
        }
    }

    // 이슈 수정
    @Override
    public void updateInGitHub(IssueEntity issue) {
        try {
            if (issue.getGithubId() == null) {
                log.warn("GitHub ID가 없는 이슈 업데이트 시도 - Issue ID: {}", issue.getIssueId());
                return;
            }

            ProjectGithubIntegrationEntity integration = getActiveIntegration(issue.getProjectEntity().getProjectId());
            GitHub gitHub = createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            GHIssue githubIssue = repository.getIssue(issue.getGithubId().intValue());

            // 제목과 내용 업데이트
            githubIssue.setTitle(issue.getName());
            if (issue.getContent() != null) {
                githubIssue.setBody(issue.getContent());
            }

            // 상태 업데이트
            if (issue.getStatus() == IssueStatus.COMPLETED && githubIssue.getState() == GHIssueState.OPEN) {
                githubIssue.close();
            } else if (issue.getStatus() != IssueStatus.COMPLETED && githubIssue.getState() == GHIssueState.CLOSED) {
                githubIssue.reopen();
            }

            // 담당자 업데이트
            updateAssignee(githubIssue, issue, gitHub);

            // 마일스톤 업데이트
            updateMilestone(githubIssue, issue, repository);

            // 동기화 시간 업데이트
            issue.updateLastSyncTime();
            issueRepository.save(issue);

            log.info("GitHub 이슈 업데이트 완료 - Issue ID: {}, GitHub Number: {}",
                    issue.getIssueId(), issue.getGithubId());

        } catch (IOException e) {
            log.error("GitHub 이슈 업데이트 실패 - Issue ID: {}", issue.getIssueId(), e);
            throw new RuntimeException("GitHub 이슈 업데이트 실패", e);
        }
    }

    // 이슈 삭제
    @Override
    public void deleteInGitHub(IssueEntity issue) {
        try {
            if (issue.getGithubId() == null) {
                log.debug("GitHub ID가 없는 이슈 삭제 시도 건너뛰기 - Issue ID: {}", issue.getId());
                return;
            }

            ProjectGithubIntegrationEntity integration = getActiveIntegration(issue.getProjectEntity().getProjectId());
            GitHub gitHub = createGitHubClient(integration);
            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            GHIssue githubIssue;
            try {
                githubIssue = repository.getIssue(issue.getGithubId().intValue());
            } catch (IOException e) {
                if (isNotFoundError(e)) {
                    log.warn("GitHub에서 이슈를 찾을 수 없음 - Issue ID: {}, GitHub ID: {}",
                            issue.getId(), issue.getGithubId());
                    return;
                }
                throw e; // 다른 에러는 상위로 전파
            }

            // 이미 삭제 처리된 이슈인지 확인
            if (isAlreadyMarkedAsDeleted(githubIssue)) {
                log.info("이미 삭제 처리된 GitHub 이슈 - Issue ID: {}, GitHub Number: {}",
                        issue.getId(), issue.getGithubId());
                return;
            }

            // 이슈 상태를 CLOSED로 변경
            if (githubIssue.getState() == GHIssueState.OPEN) {
                githubIssue.close();
                log.debug("GitHub 이슈 상태를 CLOSED로 변경 - GitHub Number: {}", issue.getGithubId());
            }

            // 삭제 표시 및 댓글 추가
            markAsDeletedOnGitHub(githubIssue);

            log.info("GitHub 이슈 삭제 처리 완료 - Issue ID: {}, GitHub Number: {}",
                    issue.getId(), issue.getGithubId());

        } catch (IOException e) {
            log.error("GitHub 이슈 삭제 처리 실패 - Issue ID: {}, GitHub ID: {}",
                    issue.getId(), issue.getGithubId(), e);
        }
    }

    
    // 이미 삭제 표시된 이슈인지 확인
    private boolean isAlreadyMarkedAsDeleted(GHIssue githubIssue) throws IOException {
        String title = githubIssue.getTitle();
        return title != null && title.startsWith("[삭제됨]");
    }

    // GitHub 이슈를 삭제됨으로 표시
    private void markAsDeletedOnGitHub(GHIssue githubIssue) throws IOException {
        String currentTitle = githubIssue.getTitle();
        String newTitle;

        // 중복 방지: 이미 "[삭제됨]"으로 시작하지 않는 경우만 추가
        if (currentTitle.startsWith("[삭제됨]")) {
            newTitle = currentTitle; // 이미 표시된 경우 그대로 유지
        } else {
            newTitle = "[삭제됨] " + currentTitle;
        }

        // 제목 길이 제한 (GitHub 이슈 제목 최대 길이: 256자)
        if (newTitle.length() > 256) {
            newTitle = newTitle.substring(0, 253) + "...";
        }

        githubIssue.setTitle(newTitle);

        // 삭제 사유 댓글 추가 (중복 방지)
        if (!hasDeleteComment(githubIssue)) {
            String deleteComment = String.format(
                    "이 이슈는 그룹웨어에서 삭제되었습니다. (삭제 시각: %s)",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            githubIssue.comment(deleteComment);
        }
    }

    // 이미 삭제 댓글이 있는지 확인
    private boolean hasDeleteComment(GHIssue githubIssue) throws IOException {
        PagedIterable<GHIssueComment> comments = githubIssue.listComments();

        for (GHIssueComment comment : comments) {
            if (comment.getBody().contains("이 이슈는 그룹웨어에서 삭제되었습니다")) {
                return true;
            }
        }
        return false;
    }

    // 404 에러인지 확인
    private boolean isNotFoundError(IOException e) {
        return e.getMessage() != null &&
                (e.getMessage().contains("404") || e.getMessage().contains("Not Found"));
    }

    // 담당자 업데이트
    private void updateAssignee(GHIssue githubIssue, IssueEntity issue, GitHub gitHub) throws IOException {
        if (issue.getAssigneeEntity() != null) {
            UserGitHubMappingEntity githubMapping = issue.getAssigneeEntity().getUserEntity().getGithubMappingEntity();
            if (githubMapping != null) {
                GHUser user = gitHub.getUser(githubMapping.getGithubUsername());
                githubIssue.setAssignees(List.of(user));
            } else {
                githubIssue.setAssignees(List.of()); // 담당자 제거
            }
        } else {
            githubIssue.setAssignees(List.of()); // 담당자 제거
        }
    }

    // 마일스톤 업데이트
    private void updateMilestone(GHIssue githubIssue, IssueEntity issue, GHRepository repository) throws IOException {
        if (issue.getMilestoneEntity() != null && issue.getMilestoneEntity().getGithubId() != null) {
            GHMilestone milestone = repository.getMilestone(issue.getMilestoneEntity().getGithubId().intValue());
            githubIssue.setMilestone(milestone);
        } else {
            githubIssue.setMilestone(null); // 마일스톤 제거
        }
    }

    // 활성화된 GitHub 연동 정보 조회
    private ProjectGithubIntegrationEntity getActiveIntegration(Long projectId) {
        return integrationRepository.findByProjectEntity_ProjectIdAndIsActive(projectId, true)
                .orElseThrow(() -> new RuntimeException("활성화된 GitHub 연동 정보를 찾을 수 없습니다."));
    }

    // GitHub 클라이언트 생성
    private GitHub createGitHubClient(ProjectGithubIntegrationEntity integration) throws IOException {
        GitHubTokenEntity tokenEntity = gitHubTokenRepository
                .findById(integration.getMemberEntity().getUserEntity().getUserId())
                .orElseThrow(() -> new RuntimeException("GitHub 토큰을 찾을 수 없습니다."));

        return new GitHubBuilder()
                .withOAuthToken(tokenEntity.getGithubAccessToken())
                .build();
    }
}
