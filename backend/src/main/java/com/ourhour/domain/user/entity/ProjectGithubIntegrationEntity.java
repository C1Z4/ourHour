package com.ourhour.domain.user.entity;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.member.entity.MemberEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tbl_project_github_integration")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectGithubIntegrationEntity extends GitHubSyncableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long integrationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Column(name = "github_repository", nullable = false)
    private String githubRepository; // "owner/repo" 형식

    @Column(name = "default_token_user_id")
    private Long defaultTokenUserId; // 기본 토큰 사용자 (백업용)

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public void updateRepository(String githubRepository) {
        this.githubRepository = githubRepository;
    }

    public void updateDefaultTokenUser(Long defaultTokenUserId) {
        this.defaultTokenUserId = defaultTokenUserId;
    }

    @Override
    public Long getId() {
        return integrationId;
    }
}
