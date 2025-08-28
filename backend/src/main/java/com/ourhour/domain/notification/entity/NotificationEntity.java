package com.ourhour.domain.notification.entity;

import com.ourhour.domain.notification.enums.NotificationType;
import com.ourhour.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Long relatedId;

    private String relatedType;

    private String actionUrl;

    private String relatedProjectName;

    @Builder
    public NotificationEntity(UserEntity userEntity, NotificationType type, String title, String message,
            Long relatedId, String relatedType, String actionUrl, String relatedProjectName, 
            boolean isRead, LocalDateTime createdAt) {
        this.userEntity = userEntity;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
        this.actionUrl = actionUrl;
        this.relatedProjectName = relatedProjectName;
        this.isRead = isRead;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public boolean getIsRead() {
        return this.isRead;
    }
}