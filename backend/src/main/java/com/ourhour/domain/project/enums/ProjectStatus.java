package com.ourhour.domain.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    NOT_STARTED("시작전"),
    PLANNING("예정됨"),
    IN_PROGRESS("진행중"),
    DONE("완료"),
    ARCHIVE("아카이브");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
