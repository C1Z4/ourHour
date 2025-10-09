package com.ourhour.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostNotificationContext {
    private Long userId;
    private String postTitle;
    private Long postId;
    private Long boardId;
    private Long orgId;
    private String commenterName; // 댓글/답글용
}
