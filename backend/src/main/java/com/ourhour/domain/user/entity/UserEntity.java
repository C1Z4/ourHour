package com.ourhour.domain.user.entity;

import com.ourhour.domain.auth.entity.EmailVerificationEntity;
import com.ourhour.domain.auth.entity.RefreshTokenEntity;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.user.enums.Platform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tbl_user")
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

    private String email;

    @Setter
    private String password;

    @Enumerated(EnumType.STRING)
    @Setter
    private Platform platform;

    private boolean isEmailVerified;
    private LocalDateTime emailVerifiedAt;

    @Builder
    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }
}