package com.ourhour.domain.project.enums;

public enum SyncStatus {
    NOT_SYNCED("연동 안 됨"),
    SYNCING("동기화 중"),
    SYNCED("동기화 완료"),
    SYNC_FAILED("동기화 실패"),
    CONFLICT("충돌");

    private final String description;

    SyncStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
