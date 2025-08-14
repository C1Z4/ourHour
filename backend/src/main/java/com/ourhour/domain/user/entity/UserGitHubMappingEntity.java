package com.ourhour.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_user_github_mapping")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGitHubMappingEntity {

    @Id
    @Column(name = "user_id")
    private Long userId; // UserEntity의 ID 참조 (기본키)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, insertable = false, updatable = false)
    private UserEntity userEntity; // UserEntity 참조

    @Column(name = "github_username", nullable = false)
    private String githubUsername; // GitHub 사용자명 (e.g., "hong-gildong")

    @Column(name = "github_email")
    private String githubEmail; // GitHub 이메일 (선택적, 검증용)

    @Column(name = "verified", nullable = false)
    @Builder.Default
    private Boolean verified = false; // 연동 검증 여부

    @Column(name = "linked_at")
    private LocalDateTime linkedAt; // 연동 시각

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public void setGithubEmail(String githubEmail) {
        this.githubEmail = githubEmail;
    }

    public void verify() {
        this.verified = true;
        this.linkedAt = LocalDateTime.now();
    }
}
