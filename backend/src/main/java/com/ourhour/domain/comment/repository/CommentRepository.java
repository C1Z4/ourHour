package com.ourhour.domain.comment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.ourhour.domain.comment.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    
    // 최상위 댓글만 페이징 (postId 기준) 
    @Query("SELECT c FROM CommentEntity c " +
           "JOIN FETCH c.authorEntity " +
           "WHERE c.postEntity.postId = :postId AND c.parentCommentId IS NULL " +
           "ORDER BY c.createdAt ASC")
    Page<CommentEntity> findByPostIdAndParentCommentIdIsNull(Long postId, Pageable pageable);
    
    
    // 최상위 댓글만 페이징 (issueId 기준)
    @Query("SELECT c FROM CommentEntity c " +
           "JOIN FETCH c.authorEntity " +
           "WHERE c.issueEntity.issueId = :issueId AND c.parentCommentId IS NULL " +
           "ORDER BY c.createdAt ASC")
    Page<CommentEntity> findByIssueIdAndParentCommentIdIsNull(Long issueId, Pageable pageable);
    
    // 특정 최상위 댓글들과 해당 대댓글들만 조회 (postId 기준)
    @Query("""
        SELECT c FROM CommentEntity c 
        JOIN FETCH c.authorEntity
        WHERE c.postEntity.postId = :postId 
        AND (c.parentCommentId IS NULL AND c.commentId IN :parentCommentIds 
             OR c.parentCommentId IN :parentCommentIds)
        ORDER BY CASE WHEN c.parentCommentId IS NULL THEN c.commentId ELSE c.parentCommentId END ASC, 
                 c.parentCommentId ASC, 
                 c.createdAt ASC
        """)
    List<CommentEntity> findByPostIdAndParentCommentIds(@Param("postId") Long postId, 
                                                        @Param("parentCommentIds") List<Long> parentCommentIds);
    
    // 특정 최상위 댓글들과 해당 대댓글들만 조회 (issueId 기준)
    @Query("""
        SELECT c FROM CommentEntity c 
        JOIN FETCH c.authorEntity
        WHERE c.issueEntity.issueId = :issueId 
        AND (c.parentCommentId IS NULL AND c.commentId IN :parentCommentIds 
             OR c.parentCommentId IN :parentCommentIds)
        ORDER BY CASE WHEN c.parentCommentId IS NULL THEN c.commentId ELSE c.parentCommentId END ASC, 
                 c.parentCommentId ASC, 
                 c.createdAt ASC
        """)
    List<CommentEntity> findByIssueIdAndParentCommentIds(@Param("issueId") Long issueId, 
                                                         @Param("parentCommentIds") List<Long> parentCommentIds);
}
