package com.ourhour.domain.project.github;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.springframework.stereotype.Component;

import com.ourhour.domain.comment.dto.CommentDTO;
import com.ourhour.domain.user.dto.GitHubRepositoryResDTO;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.enums.IssueStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GitHubDtoMapper {

    // GHRepository -> GitHubRepositoryResDTO
    public GitHubRepositoryResDTO toRepository(GHRepository repository) {
        return GitHubRepositoryResDTO.builder()
                .id(repository.getId())
                .fullName(repository.getFullName())
                .build();
    }

    // GHMilestone -> MileStoneInfoDTO
    public MileStoneInfoDTO toMilestone(GHMilestone milestone) {
        int closed = milestone.getClosedIssues();
        int open = milestone.getOpenIssues();
        int total = closed + open;
        byte progress = (byte) (total == 0 ? 0 : Math.round((closed * 100.0f) / total));
        return new MileStoneInfoDTO(
                (long) milestone.getNumber(),
                milestone.getTitle(),
                closed,
                total,
                progress);
    }

    // GHIssue -> IssueDetailDTO
    public IssueDetailDTO toIssue(GHIssue issue) {
        Long issueId = (long) issue.getNumber();
        String name = issue.getTitle();
        String status = "IN_PROGRESS";
        Long milestoneId = null;
        String milestoneName = null;
        if (issue.getMilestone() != null) {
            milestoneId = (long) issue.getMilestone().getNumber();
            milestoneName = issue.getMilestone().getTitle();
        }
        Long assigneeId = null;
        String assigneeName = null;
        String assigneeProfileImgUrl = null;
        GHUser assignee = null;
        try {
            assignee = issue.getAssignee();
        } catch (IOException e) {
            log.warn("Failed to fetch assignee for issue #{}: {}", issue.getNumber(), e.getMessage());
        }
        if (assignee != null) {
            assigneeId = assignee.getId();
            assigneeName = assignee.getLogin();
            assigneeProfileImgUrl = assignee.getAvatarUrl();
        }
        String content = issue.getBody();

        IssueDetailDTO dto = new IssueDetailDTO();
        dto.setIssueId(issueId);
        dto.setName(name);
        dto.setStatus(IssueStatus.valueOf(status));
        dto.setMilestoneId(milestoneId);
        dto.setMilestoneName(milestoneName);
        dto.setAssigneeId(assigneeId);
        dto.setAssigneeName(assigneeName);
        dto.setAssigneeProfileImgUrl(assigneeProfileImgUrl);
        dto.setContent(content);
        return dto;
    }

    // GHIssueComment -> CommentDTO
    public CommentDTO toComment(GHIssueComment comment) {
        Long authorId = null;
        String name = null;
        String profileImgUrl = null;
        try {
            GHUser user = comment.getUser();
            if (user != null) {
                authorId = user.getId();
                name = user.getLogin();
                profileImgUrl = user.getAvatarUrl();
            }
        } catch (IOException e) {
            log.warn("Failed to fetch comment user for comment #{}: {}", comment.getId(), e.getMessage());
        }

        try {
            return new CommentDTO(
                    (long) comment.getId(),
                    authorId,
                    name,
                    profileImgUrl,
                    comment.getBody(),
                    comment.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    0L, // GitHub 댓글은 좋아요 수 0으로 초기화
                    false, // GitHub 댓글은 좋아요 기능 없음
                    List.of());
        } catch (IOException e) {
            log.error("GitHub 댓글 변환 중 오류 발생", e);
            throw new RuntimeException(e);
        }
    }
}
