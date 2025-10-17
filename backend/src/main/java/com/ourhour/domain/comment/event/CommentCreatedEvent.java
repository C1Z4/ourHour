package com.ourhour.domain.comment.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.ourhour.domain.comment.entity.CommentEntity;

/**
 * 댓글 생성 이벤트
 * 트랜잭션 커밋 후 GitHub 동기화를 위해 발행됩니다.
 */
@Getter
public class CommentCreatedEvent extends ApplicationEvent {

    private final CommentEntity comment;
    private final boolean shouldSyncToGitHub;

    public CommentCreatedEvent(Object source, CommentEntity comment, boolean shouldSyncToGitHub) {
        super(source);
        this.comment = comment;
        this.shouldSyncToGitHub = shouldSyncToGitHub;
    }
}
