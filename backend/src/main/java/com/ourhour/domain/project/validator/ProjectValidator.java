package com.ourhour.domain.project.validator;

import org.springframework.stereotype.Component;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.exception.IssueException;

/**
 * 프로젝트 관련 공통 검증 로직을 담당하는 클래스
 */
@Component
public class ProjectValidator {

    /**
     * 프로젝트 ID 유효성 검증
     */
    public void validateProjectId(Long projectId) {
        if (projectId == null || projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }
    }

    /**
     * 이슈 ID 유효성 검증
     */
    public void validateIssueId(Long issueId) {
        if (issueId == null || issueId <= 0) {
            throw IssueException.issueNotFoundException();
        }
    }

    /**
     * 이슈 태그 ID 유효성 검증
     */
    public void validateIssueTagId(Long issueTagId) {
        if (issueTagId == null || issueTagId <= 0) {
            throw IssueException.issueTagNotFoundException();
        }
    }

    /**
     * 마일스톤 ID 유효성 검증 (null 허용)
     */
    public boolean isValidMilestoneId(Long milestoneId) {
        return milestoneId != null && milestoneId > 0;
    }

    /**
     * 담당자 ID 유효성 검증 (null 허용)
     */
    public boolean isValidAssigneeId(Long assigneeId) {
        return assigneeId != null && assigneeId > 0;
    }
}
