package com.ourhour.domain.project.dto;

import com.ourhour.domain.project.entity.IssueEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueDeleteResDTO {
    private Long issueId;
    private String name;
    private Long projectId;
    private Long githubId;
    private Boolean wasGithubSynced;

    public static IssueDeleteResDTO from(IssueEntity issue) {
        return IssueDeleteResDTO.builder()
                .issueId(issue.getIssueId())
                .name(issue.getName())
                .projectId(issue.getProjectEntity().getProjectId())
                .githubId(issue.getGithubId())
                .wasGithubSynced(issue.getIsGithubSynced())
                .build();
    }
}
