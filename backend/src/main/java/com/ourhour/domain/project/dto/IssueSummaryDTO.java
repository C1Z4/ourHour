package com.ourhour.domain.project.dto;

import com.ourhour.domain.project.enums.IssueStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class IssueSummaryDTO {

    private Long issueId;
    private String name;
    private IssueStatus status = IssueStatus.BACKLOG;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeProfileImgUrl;

}