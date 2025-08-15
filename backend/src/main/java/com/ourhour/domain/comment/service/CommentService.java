package com.ourhour.domain.comment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
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
import com.ourhour.domain.comment.exception.CommentException;
import com.ourhour.domain.board.entity.PostEntity;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.board.exception.PostException;
import com.ourhour.domain.project.exception.IssueException;
import com.ourhour.domain.project.sync.GitHubSyncManager;
import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final IssueRepository issueRepository;
    private final GitHubSyncManager gitHubSyncManager;
    private final CommentLikeService commentLikeService;

    // 댓글 목록 조회
    @Cacheable(value = "comments", key = "#postId + '_' + #issueId + '_' + #currentPage + '_' + #size + '_' + #currentMemberId")
    public CommentPageResDTO getComments(Long postId, Long issueId, int currentPage, int size, Long currentMemberId) {

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

        CommentResDTO commentResDTO = commentMapper.toCommentResDTO(relevantComments, postId, issueId,
                commentLikeService, currentMemberId);

        CommentPageResDTO result = CommentPageResDTO.of(
                commentResDTO,
                parentCommentPage.getNumber(),
                parentCommentPage.getSize(),
                parentCommentPage.getTotalPages(),
                parentCommentPage.getTotalElements(),
                parentCommentPage.hasNext(),
                parentCommentPage.hasPrevious());

        return result;
    }

    // 유효성 검사
    private void validateParameters(Long postId, Long issueId) {
        if (postId != null && postId < 0) {
            throw BusinessException.of(ErrorCode.INVALID_REQUEST, "postId는 0 이상이어야 합니다.");
        }

        if (issueId != null && issueId < 0) {
            throw BusinessException.of(ErrorCode.INVALID_REQUEST, "issueId는 0 이상이어야 합니다.");
        }

        if (postId == null && issueId == null) {
            throw CommentException.commentTargetRequiredException();
        }

        if (postId != null && issueId != null) {
            throw CommentException.commentTargetConflictException();
        }
    }

    // 최상위 댓글만 페이징
    private Page<CommentEntity> getParentComments(Long postId, Long issueId, int currentPage, int size) {
        if (postId != null) {
            return commentRepository.findByPostIdAndParentCommentIdIsNull(postId,
                    PageRequest.of(currentPage - 1, size));
        } else {
            return commentRepository.findByIssueIdAndParentCommentIdIsNull(issueId,
                    PageRequest.of(currentPage - 1, size));
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
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void createComment(CommentCreateReqDTO commentCreateReqDTO, Long currentMemberId) {
        validateCreateCommentRequest(commentCreateReqDTO);

        PostEntity postEntity = null;
        IssueEntity issueEntity = null;

        // postId 또는 issueId 중 하나만 조회
        if (commentCreateReqDTO.getPostId() != null) {
            postEntity = postRepository.findById(commentCreateReqDTO.getPostId())
                    .orElseThrow(() -> PostException.postNotFoundException());
        }

        if (commentCreateReqDTO.getIssueId() != null) {
            issueEntity = issueRepository.findById(commentCreateReqDTO.getIssueId())
                    .orElseThrow(() -> IssueException.issueNotFoundException());
        }

        CommentEntity commentEntity = CommentEntity.builder()
                .postEntity(postEntity)
                .issueEntity(issueEntity)
                .authorEntity(memberRepository.findById(currentMemberId)
                        .orElseThrow(() -> MemberException.memberNotFoundException()))
                .parentCommentId(commentCreateReqDTO.getParentCommentId())
                .content(commentCreateReqDTO.getContent())
                .build();

        commentRepository.save(commentEntity);

        // GitHub에도 동기화 (이슈 댓글인 경우에만)
        if (issueEntity != null) {
            gitHubSyncManager.syncToGitHub(commentEntity, SyncOperation.CREATE);
        }

    }

    // 댓글 생성 요청 검증
    private void validateCreateCommentRequest(CommentCreateReqDTO request) {
        if (request.getPostId() == null && request.getIssueId() == null) {
            throw CommentException.commentTargetRequiredException();
        }

        if (request.getPostId() != null && request.getIssueId() != null) {
            throw CommentException.commentTargetConflictException();
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw CommentException.commentContentRequiredException();
        }

        if (request.getContent().length() > 1000) {
            throw CommentException.commentContentTooLongException();
        }
    }

    // 댓글 수정
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void updateComment(Long commentId, CommentUpdateReqDTO commentUpdateReqDTO, Long currentMemberId) {
        validateUpdateCommentRequest(commentId, commentUpdateReqDTO);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.commentNotFoundException());

        commentMapper.updateCommentEntity(commentEntity, commentUpdateReqDTO);

        commentRepository.save(commentEntity);

        if (commentEntity.getIssueEntity() != null) {
            gitHubSyncManager.syncToGitHub(commentEntity, SyncOperation.UPDATE);
        }
    }

    // 댓글 수정 요청 검증
    private void validateUpdateCommentRequest(Long commentId, CommentUpdateReqDTO request) {

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw CommentException.commentContentRequiredException();
        }

    }

    // 댓글 삭제
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void deleteComment(Long commentId, Long currentMemberId) {

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.commentNotFoundException());

        if (!commentEntity.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            throw CommentException.commentAuthorRequiredException();
        }

        if (commentEntity.getIssueEntity() != null) {
            gitHubSyncManager.syncToGitHub(commentEntity, SyncOperation.DELETE);
        }

        commentRepository.delete(commentEntity);
    }
}
