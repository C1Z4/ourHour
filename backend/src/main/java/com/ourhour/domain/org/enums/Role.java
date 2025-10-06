package com.ourhour.domain.org.enums;

public enum Role {
    ROOT_ADMIN("루트관리자", 3),
    ADMIN("관리자", 2),
    MEMBER("일반회원", 1),
    GUEST("게스트", 0);

    private final String description;
    private final int level;

    Role(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHigherThan(Role accessLevel) {
        return this.level >= accessLevel.level;
    }
    public boolean isAdminOrAbove() {
        return this == ADMIN || this == ROOT_ADMIN;
    }
}

