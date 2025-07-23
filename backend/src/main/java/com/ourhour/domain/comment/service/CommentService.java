package com.ourhour.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.mapper.CommentMapper;
import com.ourhour.domain.comment.dto.CommentCreateReqDTO;
import com.ourhour.domain.comment.dto.CommentPageResDTO;
import com.ourhour.domain.comment.dto.CommentResDTO;
import com.ourhour.domain.comment.dto.CommentUpdateReqDTO;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.domain.board.entity.PostEntity;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final IssueRepository issueRepository;

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
            return commentRepository.findByPostIdAndParentCommentIdIsNull(postId, PageRequest.of(currentPage - 1, size));
        } else {
            return commentRepository.findByIssueIdAndParentCommentIdIsNull(issueId, PageRequest.of(currentPage - 1, size));
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

    // 댓글 등록
    @Transactional
    public void createComment(CommentCreateReqDTO commentCreateReqDTO) {
        validateCreateCommentRequest(commentCreateReqDTO);

        // 작성자 조회
        MemberEntity authorEntity = memberRepository.findById(commentCreateReqDTO.getAuthorId())
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 사용자입니다."));

        PostEntity postEntity = null;
        IssueEntity issueEntity = null;

        // postId 또는 issueId 중 하나만 조회
        if (commentCreateReqDTO.getPostId() != null) {
            postEntity = postRepository.findById(commentCreateReqDTO.getPostId())
                    .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 게시글입니다."));
        }

        if (commentCreateReqDTO.getIssueId() != null) {
            issueEntity = issueRepository.findById(commentCreateReqDTO.getIssueId())
                    .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 이슈입니다."));
        }

        CommentEntity commentEntity = CommentEntity.builder()
                .postEntity(postEntity)
                .issueEntity(issueEntity)
                .authorEntity(authorEntity)
                .parentCommentId(commentCreateReqDTO.getParentCommentId())
                .content(commentCreateReqDTO.getContent())
                .build();

        commentRepository.save(commentEntity);
        
    }

    // 댓글 생성 요청 검증
    private void validateCreateCommentRequest(CommentCreateReqDTO request) {
        if (request.getPostId() == null && request.getIssueId() == null) {
            throw BusinessException.badRequest("postId 또는 issueId 중 하나는 필수입니다.");
        }

        if (request.getPostId() != null && request.getIssueId() != null) {
            throw BusinessException.badRequest("postId 또는 issueId 중 하나만 입력해주세요.");
        }

        if (request.getAuthorId() == null) {
            throw BusinessException.badRequest("작성자 ID는 필수입니다.");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw BusinessException.badRequest("댓글 내용은 필수입니다.");
        }

        if (request.getContent().length() > 1000) {
            throw BusinessException.badRequest("댓글 내용은 1000자를 초과할 수 없습니다.");
        }
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentUpdateReqDTO commentUpdateReqDTO) {
        validateUpdateCommentRequest(commentId, commentUpdateReqDTO);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 댓글입니다."));

        commentMapper.updateCommentEntity(commentEntity, commentUpdateReqDTO);

        commentRepository.save(commentEntity);
    }

    // 댓글 수정 요청 검증      
    private void validateUpdateCommentRequest(Long commentId, CommentUpdateReqDTO request) {

        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw BusinessException.badRequest("댓글 내용은 필수입니다.");
        }

        if (request.getAuthorId() == null) {
            throw BusinessException.badRequest("작성자 ID는 필수입니다.");
        }

    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 댓글입니다."));
        
        commentRepository.delete(commentEntity);
    }
}
