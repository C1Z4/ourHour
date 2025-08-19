package com.ourhour.domain.user.entity;

import com.ourhour.domain.auth.entity.RefreshTokenEntity;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgInvEntity;
import com.ourhour.domain.user.enums.Platform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberEntity> memberEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokenEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "acceptedUserEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrgInvEntity> orgInvEntityList = new ArrayList<>();

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserGitHubMappingEntity githubMappingEntity;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String oauthId;

    private boolean isEmailVerified;

    private LocalDateTime emailVerifiedAt;

    private boolean isDeleted;

    @Builder
    public UserEntity(String email, String password, Platform platform, String oauthId, boolean isEmailVerified,
            LocalDateTime emailVerifiedAt) {
        this.email = email;
        this.password = password;
        this.platform = platform;
        this.oauthId = oauthId;
        this.isEmailVerified = isEmailVerified;
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public void changePassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void removeGithubMapping() {
        this.githubMappingEntity = null;
    }

}