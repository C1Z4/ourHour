package com.ourhour.domain.comment.service;

import java.util.List;
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
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.project.enums.SyncStatus;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.board.exception.PostException;
import com.ourhour.domain.project.exception.IssueException;
import com.ourhour.domain.project.annotation.GitHubSync;
import com.ourhour.domain.project.sync.GitHubSyncManager;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final IssueRepository issueRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    private final GitHubSyncManager gitHubSyncManager;

    private final CommentLikeService commentLikeService;

    // 댓글 목록 조회
    @Cacheable(value = "comments", key = "#postId + '_' + #issueId + '_' + #currentPage + '_' + #size + '_' + #currentMemberId")
    public CommentPageResDTO getComments(Long postId, Long issueId, int currentPage, int size, Long currentMemberId) {

        if (postId == null && issueId == null) {
            throw CommentException.commentTargetRequiredException();
        }

        if (postId != null && issueId != null) {
            throw CommentException.commentTargetConflictException();
        }

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
                parentCommentPage.getNumber() + 1,
                parentCommentPage.getSize(),
                parentCommentPage.getTotalPages(),
                parentCommentPage.getTotalElements(),
                parentCommentPage.hasNext(),
                parentCommentPage.hasPrevious());

        return result;
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
    @GitHubSync(operation = SyncOperation.CREATE)
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void createComment(CommentCreateReqDTO commentCreateReqDTO, Long currentMemberId) {

        if (commentCreateReqDTO.getPostId() == null && commentCreateReqDTO.getIssueId() == null) {
            throw CommentException.commentTargetRequiredException();
        }

        if (commentCreateReqDTO.getPostId() != null && commentCreateReqDTO.getIssueId() != null) {
            throw CommentException.commentTargetConflictException();
        }

        if (commentCreateReqDTO.getContent() == null || commentCreateReqDTO.getContent().trim().isEmpty()) {
            throw CommentException.commentContentRequiredException();
        }

        if (commentCreateReqDTO.getContent().length() > 1000) {
            throw CommentException.commentContentTooLongException();
        }

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

        // GitHub 연동 상태 결정
        boolean shouldSyncToGitHub = issueEntity != null && issueEntity.getIsGithubSynced();

        CommentEntity commentEntity = CommentEntity.builder()
                .postEntity(postEntity)
                .issueEntity(issueEntity)
                .authorEntity(memberRepository.findById(currentMemberId)
                        .orElseThrow(() -> MemberException.memberNotFoundException()))
                .parentCommentId(commentCreateReqDTO.getParentCommentId())
                .content(commentCreateReqDTO.getContent())
                .isGithubSynced(false) // 생성 시점에는 아직 동기화되지 않음
                .syncStatus(SyncStatus.NOT_SYNCED)
                .build();

        commentRepository.save(commentEntity);

        // GitHub에도 동기화 (이슈 댓글인 경우에만)
        if (shouldSyncToGitHub) {
            // 동기화 상태를 SYNCING으로 변경 후 GitHub 동기화 시작
            commentEntity.setSyncStatus(SyncStatus.SYNCING);
            commentRepository.save(commentEntity);
            gitHubSyncManager.syncToGitHub(commentEntity, SyncOperation.CREATE);
        }

    }

    // 댓글 수정
    @GitHubSync(operation = SyncOperation.UPDATE)
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void updateComment(Long commentId, CommentUpdateReqDTO commentUpdateReqDTO, Long currentMemberId) {

        // 댓글 수정
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.commentNotFoundException());

        // 본인이 작성한 댓글인지 확인
        if (!commentEntity.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            throw CommentException.commentAuthorRequiredException();
        }

        if (commentUpdateReqDTO.getContent() == null || commentUpdateReqDTO.getContent().trim().isEmpty()) {
            throw CommentException.commentContentRequiredException();
        }

        if (commentUpdateReqDTO.getContent().length() > 1000) {
            throw CommentException.commentContentTooLongException();
        }

        commentMapper.updateCommentEntity(commentEntity, commentUpdateReqDTO);

        commentRepository.save(commentEntity);

        if (commentEntity.getIssueEntity() != null) {
            gitHubSyncManager.syncToGitHub(commentEntity, SyncOperation.UPDATE);
        }
    }

    // 댓글 삭제
    @GitHubSync(operation = SyncOperation.DELETE)
    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void deleteComment(Long orgId, Long commentId, Long currentMemberId) {

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.commentNotFoundException());

        // 본인이 작성한 댓글이거나, 관리자 이상의 권한을 갖고 있는 경우
        if (!canDeleteComment(orgId, commentEntity, currentMemberId)) {
            throw CommentException.commentAuthorRequiredException();
        }

        if (commentEntity.getIssueEntity() != null) {
            gitHubSyncManager.syncToGitHub(commentEntity, SyncOperation.DELETE);
        }

        commentRepository.delete(commentEntity);
    }

    private boolean canDeleteComment(Long orgId, CommentEntity commentEntity, Long currentMemberId) {
        // 본인이 작성한 댓글인 경우
        if (commentEntity.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            return true;
        }

        // 현재 사용자의 해당 조직에서의 권한 확인
        return orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, currentMemberId, Status.ACTIVE)
                .map(opm -> opm.getRole().equals(Role.ADMIN) || opm.getRole().equals(Role.ROOT_ADMIN))
                .orElse(false);
    }
}
