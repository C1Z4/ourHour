package com.ourhour.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_github_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GitHubTokenEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "access_token", nullable = false)
    private String githubAccessToken;

    @Builder
    public GitHubTokenEntity(Long userId, String githubAccessToken) {
        this.userId = userId;
        setAccessToken(githubAccessToken);
    }

    private void setAccessToken(String accessToken) {
        this.githubAccessToken = accessToken;
    }

    public void updateToken(String accessToken) {
        this.githubAccessToken = accessToken;
    }
}