package com.backend.global.common.enums;

public enum Role {
    ROOT_ADMIN("최고관리자"),
    ADMIN("관리자"),
    MEMBER("일반회원");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

