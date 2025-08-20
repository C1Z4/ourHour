package com.ourhour.domain.comment.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.entity.CommentLikeEntity;
import com.ourhour.domain.comment.entity.CommentLikeId;
import com.ourhour.domain.comment.repository.CommentLikeRepository;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.domain.comment.exception.CommentException;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.member.exception.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    // 댓글 좋아요
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void likeComment(Long commentId, Long memberId) {
        
        // 댓글 존재 확인
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.commentNotFoundException());

        // 회원 존재 확인
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberException.memberNotFoundException());

        // 이미 좋아요를 눌렀는지 확인
        CommentLikeId commentLikeId = new CommentLikeId(commentId, memberId);
        if (commentLikeRepository.existsById(commentLikeId)) {
            throw CommentException.commentAlreadyLikedException();
        }

        // 좋아요 생성
        CommentLikeEntity commentLikeEntity = CommentLikeEntity.builder()
                .commentLikeId(commentLikeId)
                .commentEntity(commentEntity)
                .memberEntity(memberEntity)
                .build();

        commentLikeRepository.save(commentLikeEntity);
    }

    // 댓글 좋아요 취소
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void unlikeComment(Long commentId, Long memberId) {
        
        // 댓글 존재 확인
        if (!commentRepository.existsById(commentId)) {
            throw CommentException.commentNotFoundException();
        }

        // 회원 존재 확인
        if (!memberRepository.existsById(memberId)) {
            throw MemberException.memberNotFoundException();
        }

        // 좋아요 존재 확인
        CommentLikeId commentLikeId = new CommentLikeId(commentId, memberId);
        if (!commentLikeRepository.existsById(commentLikeId)) {
            throw CommentException.commentLikeNotFoundException();
        }

        // 좋아요 삭제
        commentLikeRepository.deleteById(commentLikeId);
    }

    // 댓글 좋아요 수 조회
    public Long getLikeCount(Long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }

    // 특정 사용자가 댓글에 좋아요를 눌렀는지 확인
    public boolean isLikedByMember(Long commentId, Long memberId) {
        CommentLikeId commentLikeId = new CommentLikeId(commentId, memberId);
        return commentLikeRepository.existsById(commentLikeId);
    }
}
