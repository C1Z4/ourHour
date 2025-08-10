package com.ourhour.domain.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ourhour.domain.project.enums.SyncStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class GitHubSyncableEntity {

    /**
     * GitHub에서의 고유 ID
     * - 이슈/마일스톤: GitHub number 사용
     * - 댓글: GitHub comment ID 사용
     */
    @Column(name = "github_id")
    private Long githubId;

    // GitHub 연동 여부
    @Column(name = "is_github_synced", nullable = false)
    private Boolean isGithubSynced = false;

    // 마지막 동기화 시간
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    /**
     * GitHub API ETag (버전 관리용)
     * API 응답의 ETag를 저장하여 변경 감지에 활용
     */
    @Column(name = "github_etag")
    private String githubEtag;

    // 동기화 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    private SyncStatus syncStatus = SyncStatus.NOT_SYNCED;

    // 공통 생성/수정 시간
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 엔티티의 고유 ID를 반환하는 추상 메서드
     * 각 엔티티에서 구현해야 함
     */
    public abstract Long getId();

    // GitHub 동기화 완료 표시
    public void markAsSynced(Long githubId) {
        this.githubId = githubId;
        this.isGithubSynced = true;
        this.lastSyncedAt = LocalDateTime.now();
        this.syncStatus = SyncStatus.SYNCED;
    }

    // 마지막 동기화 시간 업데이트
    public void updateLastSyncTime() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}
