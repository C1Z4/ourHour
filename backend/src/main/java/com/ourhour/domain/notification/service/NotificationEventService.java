package com.ourhour.domain.notification.service;

import com.ourhour.domain.notification.config.NotificationMessageTemplate;
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

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventService {

        private final NotificationService notificationService;
        private final SSENotificationService sseNotificationService;
        private final Map<NotificationType, NotificationMessageTemplate> notificationTemplates;

        // 알림 생성 및 전송 공통 메소드
        private void createAndSendNotification(NotificationCreateReqDTO dto) {
                NotificationDTO notification = notificationService.createNotification(dto);
                sseNotificationService.sendNotification(dto.getUserId(), notification);
        }

        // 채팅방 메시지 알림
        public void sendChatMessageNotification(ChatNotificationContext context) {
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.CHAT_MESSAGE);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.CHAT_MESSAGE)
                                .title(template.formatTitle(context.getSenderName()))
                                .message(template.formatMessage(context.getRoomName()))
                                .relatedId(context.getRoomId())
                                .relatedType(RelatedType.CHATROOM.getValue())
                                .actionUrl(String.format("/org/%d/chat/%d", context.getOrgId(), context.getRoomId()))
                                .build();

                createAndSendNotification(dto);
        }

        // 이슈 할당 알림
        public void sendIssueAssignedNotification(IssueNotificationContext context) {
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.ISSUE_ASSIGNED);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.ISSUE_ASSIGNED)
                                .title(template.formatTitle())
                                .message(template.formatMessage(context.getIssueTitle()))
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
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.ISSUE_COMMENT);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.ISSUE_COMMENT)
                                .title(template.formatTitle())
                                .message(template.formatMessage(context.getCommenterName(), context.getIssueTitle()))
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
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.ISSUE_COMMENT_REPLY);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.ISSUE_COMMENT_REPLY)
                                .title(template.formatTitle())
                                .message(template.formatMessage(context.getCommenterName(), context.getIssueTitle()))
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
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.POST_COMMENT);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.POST_COMMENT)
                                .title(template.formatTitle())
                                .message(template.formatMessage(context.getCommenterName(), context.getPostTitle()))
                                .relatedId(context.getPostId())
                                .relatedType(RelatedType.POST.getValue())
                                .actionUrl(String.format("/org/%d/board/%d/post/%d",
                                        context.getOrgId(), context.getBoardId(), context.getPostId()))
                                .build();

                createAndSendNotification(dto);
        }

        // 게시글 댓글 답글 알림
        public void sendPostCommentReplyNotification(PostNotificationContext context) {
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.POST_COMMENT_REPLY);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(context.getUserId())
                                .type(NotificationType.POST_COMMENT_REPLY)
                                .title(template.formatTitle())
                                .message(template.formatMessage(context.getCommenterName(), context.getPostTitle()))
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
                NotificationMessageTemplate template = notificationTemplates.get(NotificationType.COMMENT_REPLY);

                NotificationCreateReqDTO dto = NotificationCreateReqDTO.builder()
                                .userId(userId)
                                .type(NotificationType.COMMENT_REPLY)
                                .title(template.formatTitle())
                                .message(template.formatMessage(replierName))
                                .relatedId(relatedId)
                                .relatedType(relatedType)
                                .actionUrl(actionUrl)
                                .build();

                createAndSendNotification(dto);
        }
}