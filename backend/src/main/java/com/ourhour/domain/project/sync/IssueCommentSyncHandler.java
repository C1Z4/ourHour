package com.ourhour.domain.project.sync;

import java.io.IOException;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Component;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.github.GitHubClientFactory;
import com.ourhour.domain.project.github.GitHubUtil;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IssueCommentSyncHandler implements GitHubSyncHandler<CommentEntity> {

    private final GitHubUtil gitHubUtil;
    private final GitHubClientFactory gitHubClientFactory;
    private final CommentRepository commentRepository;

    @Override
    public void createInGitHub(CommentEntity comment) {
        try {
            IssueEntity issue = comment.getIssueEntity();
            if (issue == null) {
                log.debug("이슈에 속하지 않은 댓글은 GitHub 동기화 대상이 아님 - Comment ID: {}", comment.getCommentId());
                return;
            }

            if (issue.getGithubId() == null) {
                log.debug("GitHub에 동기화되지 않은 이슈의 댓글 - Issue ID: {}", issue.getId());
                return;
            }

            ProjectGithubIntegrationEntity integration = gitHubUtil.getActiveIntegration(
                    issue.getProjectEntity().getProjectId());
            GitHub gitHub = gitHubClientFactory.createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            GHIssue ghIssue = repository.getIssue(issue.getGithubId().intValue());

            GHIssueComment ghComment = ghIssue.comment(comment.getContent());

            // 댓글의 githubId는 코멘트 고유 ID를 사용
            comment.markAsSynced(ghComment.getId());
            commentRepository.save(comment);

            log.info("GitHub 댓글 생성 완료 - Comment ID: {}, GitHub Comment ID: {}",
                    comment.getCommentId(), ghComment.getId());

        } catch (IOException e) {
            log.error("GitHub 댓글 생성 실패 - Comment ID: {}", comment.getCommentId(), e);
            throw GithubException.githubSyncFailedException();
        }
    }

    @Override
    public void updateInGitHub(CommentEntity comment) {
        try {
            IssueEntity issue = comment.getIssueEntity();
            if (issue == null || issue.getGithubId() == null) {
                log.debug("이슈 정보가 없어 GitHub 댓글 업데이트 생략 - Comment ID: {}", comment.getCommentId());
                return;
            }

            if (comment.getGithubId() == null) {
                log.debug("GitHub ID가 없어 업데이트 대신 생성 시도 - Comment ID: {}", comment.getCommentId());
                createInGitHub(comment);
                return;
            }

            ProjectGithubIntegrationEntity integration = gitHubUtil.getActiveIntegration(
                    issue.getProjectEntity().getProjectId());
            GitHub gitHub = gitHubClientFactory.createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            GHIssue ghIssue = repository.getIssue(issue.getGithubId().intValue());

            // 댓글 ID로 찾아 업데이트 (리스트에서 검색)
            GHIssueComment ghComment = findCommentById(ghIssue, comment.getGithubId());
            if (ghComment == null) {
                log.warn("GitHub 댓글을 찾지 못해 재생성 시도 - Comment ID: {}, GitHub Comment ID: {}",
                        comment.getCommentId(), comment.getGithubId());
                createInGitHub(comment);
                return;
            }

            ghComment.update(comment.getContent());

            log.info("GitHub 댓글 업데이트 완료 - Comment ID: {}, GitHub Comment ID: {}",
                    comment.getCommentId(), comment.getGithubId());

        } catch (IOException e) {
            log.error("GitHub 댓글 업데이트 실패 - Comment ID: {}", comment.getCommentId(), e);
            throw GithubException.githubSyncFailedException();
        }
    }

    @Override
    public void deleteInGitHub(CommentEntity comment) {
        try {
            IssueEntity issue = comment.getIssueEntity();
            if (issue == null || issue.getGithubId() == null) {
                log.debug("이슈 정보가 없어 GitHub 댓글 삭제 생략 - Comment ID: {}", comment.getCommentId());
                return;
            }

            if (comment.getGithubId() == null) {
                log.debug("GitHub ID가 없는 댓글 삭제 시도 건너뛰기 - Comment ID: {}", comment.getCommentId());
                return;
            }

            ProjectGithubIntegrationEntity integration = gitHubUtil.getActiveIntegration(
                    issue.getProjectEntity().getProjectId());
            GitHub gitHub = gitHubClientFactory.createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            GHIssue ghIssue = repository.getIssue(issue.getGithubId().intValue());

            GHIssueComment ghComment = findCommentById(ghIssue, comment.getGithubId());
            if (ghComment == null) {
                log.warn("GitHub 댓글을 찾지 못해 삭제 생략 - Comment ID: {}, GitHub Comment ID: {}",
                        comment.getCommentId(), comment.getGithubId());
                return;
            }
            ghComment.delete();

            log.info("GitHub 댓글 삭제 완료 - Comment ID: {}, GitHub Comment ID: {}",
                    comment.getCommentId(), comment.getGithubId());

        } catch (IOException e) {
            log.error("GitHub 댓글 삭제 실패 - Comment ID: {}", comment.getCommentId(), e);
            throw GithubException.githubSyncFailedException();
        }
    }

    private GHIssueComment findCommentById(GHIssue issue, Long githubCommentId) throws IOException {
        PagedIterable<GHIssueComment> comments = issue.listComments();
        for (GHIssueComment c : comments) {
            if (c.getId() == githubCommentId) {
                return c;
            }
        }
        return null;
    }
}
