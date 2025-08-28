package com.ourhour.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_user_github_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGitHubTokenEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "github_access_token", nullable = false)
    private String githubAccessToken;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public UserGitHubTokenEntity(Long userId, String githubAccessToken, String githubUsername) {
        this.userId = userId;
        this.githubAccessToken = githubAccessToken;
        this.githubUsername = githubUsername;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateToken(String githubAccessToken, String githubUsername) {
        this.githubAccessToken = githubAccessToken;
        this.githubUsername = githubUsername;
        this.updatedAt = LocalDateTime.now();
    }
}