package com.ourhour.domain.notification.enums;

public enum NotificationType {
    PROJECT_INVITATION("프로젝트 초대"),
    CHAT_INVITATION("채팅 초대"),
    CHAT_MESSAGE("채팅 메시지"),
    ISSUE_ASSIGNED("이슈 할당"),
    ISSUE_COMMENT("이슈 댓글"),
    ISSUE_COMMENT_REPLY("이슈 댓글 답글"),
    POST_COMMENT("게시글 댓글"),
    POST_COMMENT_REPLY("게시글 댓글 답글"),
    COMMENT_REPLY("댓글 답글");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}