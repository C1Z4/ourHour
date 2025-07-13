package com.ourhour.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.mapper.CommentMapper;
import com.ourhour.domain.comment.dto.CommentPageResDTO;
import com.ourhour.domain.comment.dto.CommentResDTO;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Cacheable(value = "comments", key = "#postId + '_' + #issueId + '_' + #currentPage + '_' + #size")
    public CommentPageResDTO getComments(Long postId, Long issueId, int currentPage, int size) {
        
        validateParameters(postId, issueId);

        // 최상위 댓글만 페이징
        Page<CommentEntity> parentCommentPage = getParentComments(postId, issueId, currentPage, size);

        if (parentCommentPage.isEmpty()) {
            return CommentPageResDTO.empty(postId, issueId, currentPage, size);
        }

        // 페이징된 최상위 댓글들의 ID 추출
        List<Long> parentCommentIds = parentCommentPage.getContent().stream()
                .map(CommentEntity::getCommentId)
                .collect(Collectors.toList());

        // 대댓글들만 조회
        List<CommentEntity> relevantComments = getRelevantComments(postId, issueId, parentCommentIds);

        CommentResDTO commentResDTO = commentMapper.toCommentResDTO(relevantComments, postId, issueId);
        
        CommentPageResDTO result = CommentPageResDTO.of(
            commentResDTO,
            parentCommentPage.getNumber(),
            parentCommentPage.getSize(),
            parentCommentPage.getTotalPages(),
            parentCommentPage.getTotalElements(),
            parentCommentPage.hasNext(),
            parentCommentPage.hasPrevious()
        );

        return result;
    }

    // 유효성 검사
    private void validateParameters(Long postId, Long issueId) {
        if (postId != null && postId < 0) {
            throw BusinessException.badRequest("postId는 0 이상이어야 합니다.");
        }

        if (issueId != null && issueId < 0) {
            throw BusinessException.badRequest("issueId는 0 이상이어야 합니다.");
        }
        
        if (postId == null && issueId == null) {
            throw BusinessException.badRequest("postId 또는 issueId 중 하나는 필수입니다.");
        }

        if (postId != null && issueId != null) {
            throw BusinessException.badRequest("postId 또는 issueId 중 하나만 입력해주세요.");
        }
    }

    // 최상위 댓글만 페이징
    private Page<CommentEntity> getParentComments(Long postId, Long issueId, int currentPage, int size) {
        if (postId != null) {
            return commentRepository.findByPostIdAndParentCommentIdIsNull(postId, PageRequest.of(currentPage, size));
        } else {
            return commentRepository.findByIssueIdAndParentCommentIdIsNull(issueId, PageRequest.of(currentPage, size));
        }
    }

    // 대댓글들만 조회
    private List<CommentEntity> getRelevantComments(Long postId, Long issueId, List<Long> parentCommentIds) {
        if (postId != null) {
            return commentRepository.findByPostIdAndParentCommentIds(postId, parentCommentIds);
        } else {
            return commentRepository.findByIssueIdAndParentCommentIds(issueId, parentCommentIds);
        }
    }
}
