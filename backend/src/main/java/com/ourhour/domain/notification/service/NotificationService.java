package com.ourhour.domain.notification.service;

import com.ourhour.domain.notification.dto.NotificationCreateReqDTO;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.dto.NotificationPageResDTO;
import com.ourhour.domain.notification.entity.NotificationEntity;
import com.ourhour.domain.notification.exception.NotificationException;
import com.ourhour.domain.notification.mapper.NotificationMapper;
import com.ourhour.domain.notification.repository.NotificationRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.exception.UserException;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

        private final NotificationRepository notificationRepository;
        private final UserRepository userRepository;
        private final NotificationMapper notificationMapper;
        private final CacheManager cacheManager;

        // UserEntity 조회 공통 메소드
        private UserEntity getUserOrThrow(Long userId) {
                return userRepository.findById(userId)
                                .orElseThrow(() -> UserException.userNotFoundException());
        }

        // 알림 생성
        @Transactional
        public NotificationDTO createNotification(NotificationCreateReqDTO dto) {
                UserEntity user = getUserOrThrow(dto.getUserId());

                NotificationEntity notification = notificationMapper.toEntity(dto, user);
                NotificationEntity savedNotification = notificationRepository.save(notification);

                // 읽지 않은 알림 개수 캐시 무효화
                evictUnreadCountCache(dto.getUserId());

                return notificationMapper.toDTO(savedNotification);
        }

        // 알림 목록 조회
        public NotificationPageResDTO getNotifications(Long userId, int page, int size) {
                UserEntity user = getUserOrThrow(userId);

                Pageable pageable = PageRequest.of(page - 1, size);

                Page<NotificationEntity> notificationPage = notificationRepository
                                .findByUserEntityOrderByCreatedAtDesc(user, pageable);

                List<NotificationDTO> notifications = notificationMapper.toDTOList(notificationPage.getContent());

                long unreadCount = notificationRepository.countUnreadByUser(user);

                return NotificationPageResDTO.builder()
                                .notifications(notifications)
                                .totalElements(notificationPage.getTotalElements())
                                .totalPages(notificationPage.getTotalPages())
                                .currentPage(page)
                                .size(notificationPage.getSize())
                                .unreadCount(unreadCount)
                                .hasNext(notificationPage.hasNext())
                                .build();
        }

        // 읽지 않은 알림 개수 조회
        @Cacheable(value = "unreadNotificationCount", key = "#userId")
        public long getUnreadCount(Long userId) {
                UserEntity user = getUserOrThrow(userId);

                return notificationRepository.countUnreadByUser(user);
        }

        // 알림 읽음 처리
        @Transactional
        public void markAsRead(Long notificationId, Long userId) {
                UserEntity user = getUserOrThrow(userId);

                int updatedRows = notificationRepository.markAsReadByIdAndUser(notificationId, user);

                if (updatedRows == 0) {
                        throw NotificationException.notificationNotFound();
                }

                // 읽지 않은 알림 개수 캐시 무효화
                evictUnreadCountCache(userId);

                log.info("알림 읽음 처리 완료: notificationId={}, userId={}", notificationId, userId);
        }

        // 모든 알림 읽음 처리
        @Transactional
        public int markAllAsRead(Long userId) {
                UserEntity user = getUserOrThrow(userId);

                int updatedRows = notificationRepository.markAllAsReadByUser(user);

                // 읽지 않은 알림 개수 캐시 무효화
                evictUnreadCountCache(userId);

                log.info("모든 알림 읽음 처리 완료: userId={}, count={}", userId, updatedRows);

                return updatedRows;
        }

        // 캐시 무효화 헬퍼 메서드
        private void evictUnreadCountCache(Long userId) {
                Cache cache = cacheManager.getCache("unreadNotificationCount");
                if (cache != null) {
                        cache.evict(userId);
                }
        }
}