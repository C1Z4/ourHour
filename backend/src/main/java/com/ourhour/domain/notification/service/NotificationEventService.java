package com.ourhour.domain.notification.service;

import com.ourhour.domain.notification.dto.ChatNotificationContext;
import com.ourhour.domain.notification.dto.IssueNotificationContext;
import com.ourhour.domain.notification.dto.NotificationCreateReqDTO;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.dto.PostNotificationContext;
import com.ourhour.domain.notification.enums.NotificationType;
import com.ourhour.domain.notification.enums.RelatedType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventService {

        private final NotificationService notificationService;
        private final SSENotificationService sseNotificationService;

        // 알림 생성 및 전송 공통 메소드
        private void createAndSendNotification(NotificationCreateReqDTO dto) {
                NotificationDTO notification = notificationService.createNotification(dto);
                sseNotificationService.sendNotification(dto.getUserId(), notification);
        }

        // 채팅방 메시지 알림
        public void sendChatMessageNotification(ChatNotificationContext context) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.CHAT_MESSAGE)
                                .title(String.format("%s님의 메시지", context.getSenderName()))
                                .message(String.format("'%s' 채팅방에 새 메시지가 있습니다.", context.getRoomName()))
                                .relatedId(context.getRoomId())
                                .relatedType(RelatedType.CHATROOM.getValue())
                                .actionUrl(String.format("/org/%d/chat/%d", context.getOrgId(), context.getRoomId()))
                                .build();

                createAndSendNotification(dto);
        }

        // 이슈 할당 알림
        public void sendIssueAssignedNotification(IssueNotificationContext context) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.ISSUE_ASSIGNED)
                                .title("이슈 할당")
                                .message(String.format("'%s' 이슈가 할당되었습니다.", context.getIssueTitle()))
                                .relatedId(context.getIssueId())
                                .relatedType(RelatedType.ISSUE.getValue())
                                .actionUrl(String.format("/org/%d/project/%d/issue/%d",
                                        context.getOrgId(), context.getProjectId(), context.getIssueId()))
                                .relatedProjectName(context.getProjectName())
                                .build();

                createAndSendNotification(dto);
        }

        // 이슈 댓글 알림
        public void sendIssueCommentNotification(IssueNotificationContext context) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.ISSUE_COMMENT)
                                .title("이슈 댓글")
                                .message(String.format("%s님이 '%s' 이슈에 댓글을 남겼습니다.",
                                        context.getCommenterName(), context.getIssueTitle()))
                                .relatedId(context.getIssueId())
                                .relatedType(RelatedType.ISSUE.getValue())
                                .actionUrl(String.format("/org/%d/project/%d/issue/%d",
                                        context.getOrgId(), context.getProjectId(), context.getIssueId()))
                                .relatedProjectName(context.getProjectName())
                                .build();

                createAndSendNotification(dto);
        }

        // 이슈 댓글 답글 알림
        public void sendIssueCommentReplyNotification(IssueNotificationContext context) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.ISSUE_COMMENT_REPLY)
                                .title("이슈 댓글 답글")
                                .message(String.format("%s님이 '%s' 이슈에 댓글에 답글을 남겼습니다.",
                                        context.getCommenterName(), context.getIssueTitle()))
                                .relatedId(context.getIssueId())
                                .relatedType(RelatedType.ISSUE.getValue())
                                .actionUrl(String.format("/org/%d/project/%d/issue/%d",
                                        context.getOrgId(), context.getProjectId(), context.getIssueId()))
                                .relatedProjectName(context.getProjectName())
                                .build();

                createAndSendNotification(dto);
        }

        // 게시글 댓글 알림
        public void sendPostCommentNotification(PostNotificationContext context) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.POST_COMMENT)
                                .title("게시글 댓글")
                                .message(String.format("%s님이 '%s' 게시글에 댓글을 남겼습니다.",
                                        context.getCommenterName(), context.getPostTitle()))
                                .relatedId(context.getPostId())
                                .relatedType(RelatedType.POST.getValue())
                                .actionUrl(String.format("/org/%d/board/%d/post/%d",
                                        context.getOrgId(), context.getBoardId(), context.getPostId()))
                                .build();

                createAndSendNotification(dto);
        }

        // 게시글 댓글 답글 알림
        public void sendPostCommentReplyNotification(PostNotificationContext context) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.POST_COMMENT_REPLY)
                                .title("댓글 답글")
                                .message(String.format("%s님이 '%s' 게시글의 댓글에 답글을 남겼습니다.",
                                        context.getCommenterName(), context.getPostTitle()))
                                .relatedId(context.getPostId())
                                .relatedType(RelatedType.POST.getValue())
                                .actionUrl(String.format("/org/%d/board/%d/post/%d",
                                        context.getOrgId(), context.getBoardId(), context.getPostId()))
                                .build();

                createAndSendNotification(dto);
        }

        // 댓글 답글 알림 (댓글 작성자에게)
        public void sendCommentReplyNotification(Long userId, String replierName, String originalCommentContent,
                        Long relatedId, String relatedType, String actionUrl) {
                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(userId)
                                .type(NotificationType.COMMENT_REPLY)
                                .title("댓글 답글")
                                .message(String.format("%s님이 회원님의 댓글에 답글을 남겼습니다.", replierName))
                                .relatedId(relatedId)
                                .relatedType(relatedType)
                                .actionUrl(actionUrl)
                                .build();

                createAndSendNotification(dto);
        }
}