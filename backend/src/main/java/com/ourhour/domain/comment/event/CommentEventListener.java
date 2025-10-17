package com.ourhour.domain.comment.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.project.sync.GitHubSyncManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 댓글 이벤트 리스너
 * 트랜잭션 커밋 후 GitHub 동기화를 처리합니다.
 * 외부 API 호출을 트랜잭션과 분리하여 안정성을 높입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final GitHubSyncManager gitHubSyncManager;

    /**
     * 댓글 생성 이벤트 처리
     * 트랜잭션이 성공적으로 커밋된 후에만 실행됩니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {
        if (!event.isShouldSyncToGitHub()) {
            log.debug("GitHub 동기화가 필요하지 않은 댓글입니다. commentId={}",
                event.getComment().getCommentId());
            return;
        }

        try {
            log.info("댓글 생성 후 GitHub 동기화 시작. commentId={}",
                event.getComment().getCommentId());
            gitHubSyncManager.syncToGitHub(event.getComment(), SyncOperation.CREATE);
        } catch (Exception e) {
            // GitHub 동기화 실패는 로그만 남기고 전체 트랜잭션에 영향을 주지 않음
            log.error("댓글 생성 GitHub 동기화 실패. commentId={}",
                event.getComment().getCommentId(), e);
            // TODO: 실패한 동기화를 재시도 큐에 추가하는 로직 추가 고려
        }
    }

    /**
     * 댓글 수정 이벤트 처리
     * 트랜잭션이 성공적으로 커밋된 후에만 실행됩니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentUpdated(CommentUpdatedEvent event) {
        if (!event.isShouldSyncToGitHub()) {
            log.debug("GitHub 동기화가 필요하지 않은 댓글입니다. commentId={}",
                event.getComment().getCommentId());
            return;
        }

        try {
            log.info("댓글 수정 후 GitHub 동기화 시작. commentId={}",
                event.getComment().getCommentId());
            gitHubSyncManager.syncToGitHub(event.getComment(), SyncOperation.UPDATE);
        } catch (Exception e) {
            log.error("댓글 수정 GitHub 동기화 실패. commentId={}",
                event.getComment().getCommentId(), e);
        }
    }

    /**
     * 댓글 삭제 이벤트 처리
     * 트랜잭션이 성공적으로 커밋된 후에만 실행됩니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentDeleted(CommentDeletedEvent event) {
        if (!event.isShouldSyncToGitHub()) {
            log.debug("GitHub 동기화가 필요하지 않은 댓글입니다. commentId={}",
                event.getComment().getCommentId());
            return;
        }

        try {
            log.info("댓글 삭제 후 GitHub 동기화 시작. commentId={}",
                event.getComment().getCommentId());
            gitHubSyncManager.syncToGitHub(event.getComment(), SyncOperation.DELETE);
        } catch (Exception e) {
            log.error("댓글 삭제 GitHub 동기화 실패. commentId={}",
                event.getComment().getCommentId(), e);
        }
    }
}
