package com.ourhour.domain.comment.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.entity.CommentLikeEntity;
import com.ourhour.domain.comment.entity.CommentLikeId;
import com.ourhour.domain.comment.repository.CommentLikeCount;
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

    // 댓글 좋아요 (엔티티 조회 최적화)
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void likeComment(Long commentId, Long memberId) {

        // 댓글 존재 확인 (존재 여부만 확인, 엔티티 조회 X)
        if (!commentRepository.existsById(commentId)) {
            throw CommentException.commentNotFoundException();
        }

        // 회원 존재 확인 (존재 여부만 확인, 엔티티 조회 X)
        if (!memberRepository.existsById(memberId)) {
            throw MemberException.memberNotFoundException();
        }

        // 이미 좋아요를 눌렀는지 확인
        CommentLikeId commentLikeId = new CommentLikeId(commentId, memberId);
        if (commentLikeRepository.existsById(commentLikeId)) {
            throw CommentException.commentAlreadyLikedException();
        }

        // 좋아요 생성 (JPA가 ID로 참조만 설정, 실제 엔티티 로드 안함)
        CommentLikeEntity commentLikeEntity = CommentLikeEntity.builder()
                .commentLikeId(commentLikeId)
                .commentEntity(commentRepository.getReferenceById(commentId))
                .memberEntity(memberRepository.getReferenceById(memberId))
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

    // 여러 댓글의 좋아요 수를 Map으로 반환 (N+1 방지, 타입 안전)
    public Map<Long, Long> getLikeCountsMap(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Map.of();
        }

        List<CommentLikeCount> results = commentLikeRepository.countByCommentIds(commentIds);
        Map<Long, Long> likeCountMap = results.stream()
            .collect(Collectors.toMap(
                CommentLikeCount::getCommentId,
                CommentLikeCount::getLikeCount
            ));

        // 좋아요가 없는 댓글은 0으로 설정
        commentIds.forEach(id -> likeCountMap.putIfAbsent(id, 0L));

        return likeCountMap;
    }

    // 특정 사용자가 좋아요한 댓글 ID Set 반환 (N+1 방지)
    public Set<Long> getLikedCommentIds(Long memberId, List<Long> commentIds) {
        if (memberId == null || commentIds == null || commentIds.isEmpty()) {
            return Set.of();
        }

        List<Long> likedIds = commentLikeRepository.findLikedCommentIdsByMemberAndCommentIds(memberId, commentIds);
        return new HashSet<>(likedIds);
    }
}
