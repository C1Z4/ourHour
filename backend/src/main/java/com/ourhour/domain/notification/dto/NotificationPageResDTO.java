package com.ourhour.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationPageResDTO {
    private List<NotificationDTO> notifications;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;
    private long unreadCount;
    private boolean hasNext; // 무한스크롤을 위한 다음 페이지 존재 여부
}