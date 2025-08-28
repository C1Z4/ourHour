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

        // 알림 생성
        @Transactional
        public NotificationDTO createNotification(NotificationCreateReqDTO dto) {
                UserEntity user = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> UserException.userNotFoundException());

                NotificationEntity notification = notificationMapper.toEntity(dto, user);
                NotificationEntity savedNotification = notificationRepository.save(notification);

                return notificationMapper.toDTO(savedNotification);
        }

        // 알림 목록 조회
        public NotificationPageResDTO getNotifications(Long userId, int page, int size) {
                UserEntity user = userRepository.findById(userId)
                                .orElseThrow(() -> UserException.userNotFoundException());

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
        public long getUnreadCount(Long userId) {
                UserEntity user = userRepository.findById(userId)
                                .orElseThrow(() -> UserException.userNotFoundException());

                return notificationRepository.countUnreadByUser(user);
        }

        // 알림 읽음 처리
        @Transactional
        public void markAsRead(Long notificationId, Long userId) {
                UserEntity user = userRepository.findById(userId)
                                .orElseThrow(() -> UserException.userNotFoundException());

                int updatedRows = notificationRepository.markAsReadByIdAndUser(notificationId, user);

                if (updatedRows == 0) {
                        throw NotificationException.notificationNotFound();
                }

                log.info("알림 읽음 처리 완료: notificationId={}, userId={}", notificationId, userId);
        }

        // 모든 알림 읽음 처리
        @Transactional
        public int markAllAsRead(Long userId) {
                UserEntity user = userRepository.findById(userId)
                                .orElseThrow(() -> UserException.userNotFoundException());

                int updatedRows = notificationRepository.markAllAsReadByUser(user);

                log.info("모든 알림 읽음 처리 완료: userId={}, count={}", userId, updatedRows);

                return updatedRows;
        }
}