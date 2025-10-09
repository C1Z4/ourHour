package com.ourhour.domain.notification.repository;

import com.ourhour.domain.notification.entity.NotificationEntity;
import com.ourhour.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserEntityOrderByCreatedAtDesc(UserEntity userEntity, Pageable pageable);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.userEntity = :user AND n.isRead = false")
    long countUnreadByUser(@Param("user") UserEntity user);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.userEntity = :user AND n.isRead = false")
    int markAllAsReadByUser(@Param("user") UserEntity user);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.notificationId = :notificationId AND n.userEntity = :user")
    int markAsReadByIdAndUser(@Param("notificationId") Long notificationId, @Param("user") UserEntity user);
}