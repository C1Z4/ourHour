package com.ourhour.domain.comment.repository;

/**
 * 댓글 좋아요 수 조회를 위한 Projection Interface
 */
public interface CommentLikeCount {
    Long getCommentId();
    Long getLikeCount();
}
