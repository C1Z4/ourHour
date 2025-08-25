package com.ourhour.domain.notification.service;

import com.ourhour.domain.notification.dto.NotificationCreateReqDTO;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventService {

    private final NotificationService notificationService;
    private final SSENotificationService sseNotificationService;    

    // 프로젝트 초대 알림
    // public void sendProjectInvitationNotification(Long userId, String
    // projectName, Long projectId) {
    // NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
    // .userId(userId)
    // .type(NotificationType.PROJECT_INVITATION)
    // .title("프로젝트 초대")
    // .message(String.format("'%s' 프로젝트에 초대되었습니다.", projectName))
    // .relatedId(projectId)
    // .relatedType("project")
    // .actionUrl(String.format("/project/%d", projectId))
    // .build();

    // NotificationDTO notification = notificationService.createNotification(dto);
    // sseNotificationService.sendNotification(userId, notification);
    // }

    // 채팅방 메시지 알림
    public void sendChatMessageNotification(Long userId, String senderName, String roomName, Long roomId) {
        NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                .userId(userId)
                .type(NotificationType.CHAT_MESSAGE)
                .title(String.format("%s님의 메시지", senderName))
                .message(String.format("'%s' 채팅방에 새 메시지가 있습니다.", roomName))
                .relatedId(roomId)
                .relatedType("chatroom")
                .actionUrl(String.format("/chat/room/%d", roomId))
                .build();

        NotificationDTO notification = notificationService.createNotification(dto);
        sseNotificationService.sendNotification(userId, notification);
    }

    // 이슈 할당 알림
    public void sendIssueAssignedNotification(Long userId, String issueTitle, Long issueId, Long projectId,
            Long orgId) {
        NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                .userId(userId)
                .type(NotificationType.ISSUE_ASSIGNED)
                .title("이슈 할당")
                .message(String.format("'%s' 이슈가 할당되었습니다.", issueTitle))
                .relatedId(issueId)
                .relatedType("issue")
                .actionUrl(String.format("/org/%d/project/%d/issue/%d", orgId, projectId, issueId))
                .build();

        NotificationDTO notification = notificationService.createNotification(dto);

        sseNotificationService.sendNotification(userId, notification);
    }

    // 이슈 댓글 알림
    public void sendIssueCommentNotification(Long userId, String commenterName, String issueTitle, Long issueId,
            Long projectId, Long orgId) {
        NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                .userId(userId)
                .type(NotificationType.ISSUE_COMMENT)
                .title("이슈 댓글")
                .message(String.format("%s님이 '%s' 이슈에 댓글을 남겼습니다.", commenterName, issueTitle))
                .relatedId(issueId)
                .relatedType("issue")
                .actionUrl(String.format("/org/%d/project/%d/issue/%d", orgId, projectId, issueId))
                .build();

        NotificationDTO notification = notificationService.createNotification(dto);
        sseNotificationService.sendNotification(userId, notification);
    }

    // 이슈 댓글 답글 알림
    public void sendIssueCommentReplyNotification(Long userId, String replierName, String issueTitle, Long issueId,
            Long projectId, Long orgId) {
        NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                .userId(userId)
                .type(NotificationType.ISSUE_COMMENT_REPLY)
                .title("이슈 댓글 답글")
                .message(String.format("%s님이 '%s' 이슈에 댓글에 답글을 남겼습니다.", replierName, issueTitle))
                .relatedId(issueId)
                .relatedType("issue")
                .actionUrl(String.format("/org/%d/project/%d/issue/%d", orgId, projectId, issueId))
                .build();

        NotificationDTO notification = notificationService.createNotification(dto);
        sseNotificationService.sendNotification(userId, notification);
    }

    // 게시글 댓글 알림
    public void sendPostCommentNotification(Long userId, String commenterName, String postTitle, Long postId,
            Long boardId, Long orgId) {
        NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                .userId(userId)
                .type(NotificationType.POST_COMMENT)
                .title("게시글 댓글")
                .message(String.format("%s님이 '%s' 게시글에 댓글을 남겼습니다.", commenterName, postTitle))
                .relatedId(postId)
                .relatedType("post")
                .actionUrl(String.format("/org/%d/board/%d/post/%d", orgId, boardId, postId))
                .build();

        NotificationDTO notification = notificationService.createNotification(dto);
        sseNotificationService.sendNotification(userId, notification);
    }

    // 게시글 댓글 답글 알림
    public void sendPostCommentReplyNotification(Long userId, String replierName, String postTitle, Long postId,
            Long boardId, Long orgId) {
        NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                .userId(userId)
                .type(NotificationType.POST_COMMENT_REPLY)
                .title("댓글 답글")
                .message(String.format("%s님이 '%s' 게시글의 댓글에 답글을 남겼습니다.", replierName, postTitle))
                .relatedId(postId)
                .relatedType("post")
                .actionUrl(String.format("/org/%d/board/%d/post/%d", orgId, boardId, postId))
                .build();

        NotificationDTO notification = notificationService.createNotification(dto);
        sseNotificationService.sendNotification(userId, notification);
    }
}