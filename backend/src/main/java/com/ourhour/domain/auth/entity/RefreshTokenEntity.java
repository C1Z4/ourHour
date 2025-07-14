package com.ourhour.domain.auth.entity;

import com.ourhour.domain.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Builder
    public RefreshTokenEntity(UserEntity userEntity, String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.userEntity = userEntity;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

}
