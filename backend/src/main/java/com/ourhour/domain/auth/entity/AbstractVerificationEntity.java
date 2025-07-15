package com.ourhour.domain.auth.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractVerificationEntity {

    protected String token;
    protected String email;
    protected LocalDateTime createdAt;
    protected LocalDateTime expiredAt;

    @Setter
    protected boolean isUsed;

}
