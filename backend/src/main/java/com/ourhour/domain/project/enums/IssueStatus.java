package com.ourhour.domain.project.enums;

public enum IssueStatus {
    BACKLOG("백로그"),
    NOT_STARTED("시작전"),
    PENDING("대기중"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료됨");

    private final String description;

    IssueStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
