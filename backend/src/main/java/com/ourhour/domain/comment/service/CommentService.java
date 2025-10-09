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
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.notification.dto.IssueNotificationContext;
import com.ourhour.domain.notification.dto.PostNotificationContext;
import com.ourhour.domain.notification.service.NotificationEventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int MAX_COMMENT_LENGTH = 1000;
    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final IssueRepository issueRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    private final GitHubSyncManager gitHubSyncManager;

    private final CommentLikeService commentLikeService;
    private final NotificationEventService notificationEventService;

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

        return CommentPageResDTO.of(
                commentResDTO,
                parentCommentPage.getNumber() + 1,
                parentCommentPage.getSize(),
                parentCommentPage.getTotalPages(),
                parentCommentPage.getTotalElements(),
                parentCommentPage.hasNext(),
                parentCommentPage.hasPrevious());
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

        validateCommentRequest(commentCreateReqDTO);

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

        if (postEntity != null) {
            sendPostCommentNotifications(postEntity, commentEntity, commentCreateReqDTO.getParentCommentId());
        }

        if (issueEntity != null) {
            sendIssueCommentNotifications(issueEntity, commentEntity, commentCreateReqDTO.getParentCommentId());
        }

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

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.commentNotFoundException());

        // 본인이 작성한 댓글인지 확인
        if (!commentEntity.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            throw CommentException.commentAuthorAccessDeniedException();
        }

        validateCommentContent(commentUpdateReqDTO.getContent());

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
                .map(opm -> opm.getRole().isAdminOrAbove())
                .orElse(false);
    }

    // 댓글 요청 유효성 검증
    private void validateCommentRequest(CommentCreateReqDTO commentCreateReqDTO) {
        if (commentCreateReqDTO.getPostId() == null && commentCreateReqDTO.getIssueId() == null) {
            throw CommentException.commentTargetRequiredException();
        }

        if (commentCreateReqDTO.getPostId() != null && commentCreateReqDTO.getIssueId() != null) {
            throw CommentException.commentTargetConflictException();
        }

        validateCommentContent(commentCreateReqDTO.getContent());
    }

    // 댓글 내용 유효성 검증
    private void validateCommentContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw CommentException.commentContentRequiredException();
        }

        if (content.length() > MAX_COMMENT_LENGTH) {
            throw CommentException.commentContentTooLongException();
        }
    }

    // 게시글 댓글 알림 전송
    private void sendPostCommentNotifications(PostEntity postEntity, CommentEntity commentEntity, Long parentCommentId) {
        Long authorUserId = postEntity.getAuthorEntity().getUserEntity().getUserId();
        Long currentUserId = commentEntity.getAuthorEntity().getUserEntity().getUserId();

        if (!authorUserId.equals(currentUserId)) {
            PostNotificationContext context = PostNotificationContext.builder()
                    .userId(authorUserId)
                    .postTitle(postEntity.getTitle())
                    .postId(postEntity.getPostId())
                    .boardId(postEntity.getBoardEntity().getBoardId())
                    .orgId(postEntity.getBoardEntity().getOrgEntity().getOrgId())
                    .commenterName(commentEntity.getAuthorEntity().getName())
                    .build();

            if (parentCommentId != null) {
                notificationEventService.sendPostCommentReplyNotification(context);
            } else {
                notificationEventService.sendPostCommentNotification(context);
            }
        }

        if (parentCommentId != null) {
            sendParentCommentReplyNotification(
                parentCommentId,
                currentUserId,
                authorUserId,
                commentEntity.getAuthorEntity().getName(),
                postEntity.getPostId(),
                "post",
                buildPostUrl(
                    postEntity.getBoardEntity().getOrgEntity().getOrgId(),
                    postEntity.getBoardEntity().getBoardId(),
                    postEntity.getPostId())
            );
        }
    }

    // 이슈 댓글 알림 전송
    private void sendIssueCommentNotifications(IssueEntity issueEntity, CommentEntity commentEntity, Long parentCommentId) {
        Long assigneeUserId = issueEntity.getAssigneeEntity() != null ?
            issueEntity.getAssigneeEntity().getUserEntity().getUserId() : null;
        Long currentUserId = commentEntity.getAuthorEntity().getUserEntity().getUserId();

        if (assigneeUserId != null && !assigneeUserId.equals(currentUserId)) {
            IssueNotificationContext context = IssueNotificationContext.builder()
                    .userId(assigneeUserId)
                    .issueTitle(issueEntity.getName())
                    .issueId(issueEntity.getIssueId())
                    .projectId(issueEntity.getProjectEntity().getProjectId())
                    .orgId(issueEntity.getProjectEntity().getOrgEntity().getOrgId())
                    .projectName(issueEntity.getProjectEntity().getName())
                    .commenterName(commentEntity.getAuthorEntity().getName())
                    .build();

            if (parentCommentId != null) {
                notificationEventService.sendIssueCommentReplyNotification(context);
            } else {
                notificationEventService.sendIssueCommentNotification(context);
            }
        }

        if (parentCommentId != null) {
            sendParentCommentReplyNotification(
                parentCommentId,
                currentUserId,
                assigneeUserId,
                commentEntity.getAuthorEntity().getName(),
                issueEntity.getIssueId(),
                "issue",
                buildIssueUrl(
                    issueEntity.getProjectEntity().getOrgEntity().getOrgId(),
                    issueEntity.getProjectEntity().getProjectId(),
                    issueEntity.getIssueId())
            );
        }
    }

    // 대댓글 알림 전송
    private void sendParentCommentReplyNotification(Long parentCommentId, Long currentUserId,
            Long excludeUserId, String authorName, Long targetId, String targetType, String actionUrl) {
        CommentEntity parentComment = commentRepository.findById(parentCommentId)
                .orElse(null);

        if (parentComment != null) {
            Long parentAuthorUserId = parentComment.getAuthorEntity().getUserEntity().getUserId();

            // 대댓글 작성자와 원래 댓글 작성자가 다르고, excludeUserId와도 다른 경우에만 알림 전송
            if (!parentAuthorUserId.equals(currentUserId) &&
                (excludeUserId == null || !parentAuthorUserId.equals(excludeUserId))) {
                notificationEventService.sendCommentReplyNotification(
                    parentAuthorUserId,
                    authorName,
                    parentComment.getContent(),
                    targetId,
                    targetType,
                    actionUrl
                );
            }
        }
    }

    // 게시글 URL 생성
    private String buildPostUrl(Long orgId, Long boardId, Long postId) {
        return String.format("/org/%d/board/%d/post/%d", orgId, boardId, postId);
    }

    // 이슈 URL 생성
    private String buildIssueUrl(Long orgId, Long projectId, Long issueId) {
        return String.format("/org/%d/project/%d/issue/%d", orgId, projectId, issueId);
    }

}
