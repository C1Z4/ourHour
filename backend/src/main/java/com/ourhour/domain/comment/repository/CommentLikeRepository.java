package com.ourhour.domain.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ourhour.domain.comment.entity.CommentLikeEntity;
import com.ourhour.domain.comment.entity.CommentLikeId;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, CommentLikeId> {

    // 특정 댓글의 좋아요 수 조회
    @Query("SELECT COUNT(cl) FROM CommentLikeEntity cl WHERE cl.commentLikeId.commentId = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);

    // 여러 댓글의 좋아요 수를 한번에 조회 (타입 안전한 Projection 사용)
    @Query("SELECT cl.commentLikeId.commentId as commentId, COUNT(cl) as likeCount FROM CommentLikeEntity cl " +
            "WHERE cl.commentLikeId.commentId IN :commentIds " +
            "GROUP BY cl.commentLikeId.commentId")
    List<CommentLikeCount> countByCommentIds(@Param("commentIds") List<Long> commentIds);

    // 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
    boolean existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId(Long commentId, Long memberId);

    // 특정 사용자의 댓글 좋아요 조회
    Optional<CommentLikeEntity> findByCommentLikeId_CommentIdAndCommentLikeId_MemberId(Long commentId, Long memberId);

    // 특정 사용자가 좋아요한 댓글 ID 목록 조회
    @Query("SELECT cl.commentLikeId.commentId FROM CommentLikeEntity cl " +
            "WHERE cl.commentLikeId.memberId = :memberId " +
            "AND cl.commentLikeId.commentId IN :commentIds")
    List<Long> findLikedCommentIdsByMemberAndCommentIds(@Param("memberId") Long memberId,
                                                         @Param("commentIds") List<Long> commentIds);
}

