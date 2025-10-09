package com.ourhour.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueNotificationContext {
    private Long userId;
    private String issueTitle;
    private Long issueId;
    private Long projectId;
    private Long orgId;
    private String projectName;
    private String commenterName; // 댓글/답글용
}
