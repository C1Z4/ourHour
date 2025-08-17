package com.ourhour.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.comment.dto.CommentCreateReqDTO;
import com.ourhour.domain.comment.dto.CommentPageResDTO;
import com.ourhour.domain.comment.dto.CommentResDTO;
import com.ourhour.domain.comment.dto.CommentUpdateReqDTO;
import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.exception.CommentException;
import com.ourhour.domain.comment.mapper.CommentMapper;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.domain.board.entity.PostEntity;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.sync.GitHubSyncManager;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 테스트")
class CommentServiceTest {

        @Mock
        private CommentRepository commentRepository;

        @Mock
        private CommentMapper commentMapper;

        @Mock
        private MemberRepository memberRepository;

        @Mock
        private PostRepository postRepository;

        @Mock
        private IssueRepository issueRepository;

        @Mock
        private CommentLikeService commentLikeService;

        @Mock
        private GitHubSyncManager gitHubSyncManager;

        @Mock
        private OrgParticipantMemberRepository orgParticipantMemberRepository;

        @InjectMocks
        private CommentService commentService;

        private MemberEntity member;
        private PostEntity post;
        private IssueEntity issue;
        private CommentEntity comment;
        private CommentCreateReqDTO createReqDTO;
        private CommentUpdateReqDTO updateReqDTO;

        @BeforeEach
        void setUp() {
                member = mock(MemberEntity.class);
                post = mock(PostEntity.class);
                issue = mock(IssueEntity.class);
                comment = mock(CommentEntity.class);

                createReqDTO = new CommentCreateReqDTO();
                updateReqDTO = new CommentUpdateReqDTO();
        }

        @Test
        @DisplayName("게시글 댓글 목록 조회 성공")
        void getComments_Post_Success() {
                // given
                Long postId = 1L;
                Long issueId = null;
                int currentPage = 1;
                int size = 10;
                Long currentMemberId = 1L;

                Pageable pageable = PageRequest.of(0, size); // currentPage = 1이므로 0
                Page<CommentEntity> parentComments = new PageImpl<>(List.of(comment), pageable, 1);
                List<CommentEntity> allComments = List.of(comment);

                given(commentRepository.findByPostIdAndParentCommentIdIsNull(any(), any(Pageable.class)))
                                .willReturn(parentComments);
                given(commentRepository.findByPostIdAndParentCommentIds(any(), anyList()))
                                .willReturn(allComments);
                given(commentMapper.toCommentResDTO(any(), any(), any(),
                                any(), any()))
                                .willReturn(mock(CommentResDTO.class));

                // when
                CommentPageResDTO result = commentService.getComments(postId, issueId, currentPage, size,
                                currentMemberId);

                // then
                assertThat(result).isNotNull();
                then(commentRepository).should().findByPostIdAndParentCommentIdIsNull(postId, pageable);
                then(commentRepository).should().findByPostIdAndParentCommentIds(eq(postId), anyList());
        }

        @Test
        @DisplayName("이슈 댓글 목록 조회 성공")
        void getComments_Issue_Success() {
                // given
                Long postId = null;
                Long issueId = 1L;
                int currentPage = 1;
                int size = 10;
                Long currentMemberId = 1L;

                Pageable pageable = PageRequest.of(0, size); // currentPage = 1이므로 0
                Page<CommentEntity> parentComments = new PageImpl<>(List.of(comment), pageable, 1);
                List<CommentEntity> allComments = List.of(comment);

                given(commentRepository.findByIssueIdAndParentCommentIdIsNull(any(), any(Pageable.class)))
                                .willReturn(parentComments);
                given(commentRepository.findByIssueIdAndParentCommentIds(any(), anyList()))
                                .willReturn(allComments);
                given(commentMapper.toCommentResDTO(any(), any(), any(),
                                any(), any()))
                                .willReturn(mock(CommentResDTO.class));

                // when
                CommentPageResDTO result = commentService.getComments(postId, issueId, currentPage, size,
                                currentMemberId);

                // then
                assertThat(result).isNotNull();
                then(commentRepository).should().findByIssueIdAndParentCommentIdIsNull(issueId, pageable);
                then(commentRepository).should().findByIssueIdAndParentCommentIds(eq(issueId), anyList());
        }

        @Test
        @DisplayName("댓글 목록 조회 시 postId와 issueId 모두 null인 경우 예외 발생")
        void getComments_BothIdsNull_ThrowsException() {
                // given
                Long postId = null;
                Long issueId = null;
                int currentPage = 1;
                int size = 10;
                Long currentMemberId = 1L;

                // when & then
                assertThatThrownBy(
                                () -> commentService.getComments(postId, issueId, currentPage, size, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 목록 조회 시 postId와 issueId 모두 존재하는 경우 예외 발생")
        void getComments_BothIdsPresent_ThrowsException() {
                // given
                Long postId = 1L;
                Long issueId = 1L;
                int currentPage = 1;
                int size = 10;
                Long currentMemberId = 1L;

                // when & then
                assertThatThrownBy(
                                () -> commentService.getComments(postId, issueId, currentPage, size, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("게시글 댓글 생성 성공")
        void createComment_Post_Success() {
                // given
                Long currentMemberId = 1L;
                createReqDTO.setPostId(1L);
                createReqDTO.setIssueId(null);
                createReqDTO.setContent("테스트 댓글");

                given(memberRepository.findById(currentMemberId)).willReturn(Optional.of(member));
                given(postRepository.findById(1L)).willReturn(Optional.of(post));
                given(commentRepository.save(any(CommentEntity.class))).willReturn(comment);

                // when
                commentService.createComment(createReqDTO, currentMemberId);

                // then
                then(memberRepository).should().findById(currentMemberId);
                then(postRepository).should().findById(1L);
                then(commentRepository).should().save(any(CommentEntity.class));
                then(issueRepository).should(never()).findById(anyLong());
        }

        @Test
        @DisplayName("이슈 댓글 생성 성공")
        void createComment_Issue_Success() {
                // given
                Long currentMemberId = 1L;
                createReqDTO.setPostId(null);
                createReqDTO.setIssueId(1L);
                createReqDTO.setContent("테스트 댓글");

                given(memberRepository.findById(currentMemberId)).willReturn(Optional.of(member));
                given(issueRepository.findById(1L)).willReturn(Optional.of(issue));
                given(commentRepository.save(any(CommentEntity.class))).willReturn(comment);

                // when
                commentService.createComment(createReqDTO, currentMemberId);

                // then
                then(memberRepository).should().findById(currentMemberId);
                then(issueRepository).should().findById(1L);
                then(commentRepository).should().save(any(CommentEntity.class));
                then(postRepository).should(never()).findById(anyLong());
        }

        @Test
        @DisplayName("댓글 생성 시 postId와 issueId 모두 null인 경우 예외 발생")
        void createComment_BothIdsNull_ThrowsException() {
                // given
                Long currentMemberId = 1L;
                createReqDTO.setPostId(null);
                createReqDTO.setIssueId(null);
                createReqDTO.setContent("테스트 댓글");

                // when & then
                assertThatThrownBy(() -> commentService.createComment(createReqDTO, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 생성 시 postId와 issueId 모두 존재하는 경우 예외 발생")
        void createComment_BothIdsPresent_ThrowsException() {
                // given
                Long currentMemberId = 1L;
                createReqDTO.setPostId(1L);
                createReqDTO.setIssueId(1L);
                createReqDTO.setContent("테스트 댓글");

                // when & then
                assertThatThrownBy(() -> commentService.createComment(createReqDTO, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 생성 시 내용이 비어있는 경우 예외 발생")
        void createComment_EmptyContent_ThrowsException() {
                // given
                Long currentMemberId = 1L;
                createReqDTO.setPostId(1L);
                createReqDTO.setContent(""); // 빈 내용으로 설정

                // when & then
                assertThatThrownBy(() -> commentService.createComment(createReqDTO, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 수정 성공")
        void updateComment_Success() {
                // given
                Long commentId = 1L;
                Long currentMemberId = 1L;
                updateReqDTO.setContent("테스트 댓글 수정");

                given(member.getMemberId()).willReturn(1L);
                given(comment.getAuthorEntity()).willReturn(member);

                given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
                given(commentRepository.save(any(CommentEntity.class))).willReturn(comment);

                // when
                commentService.updateComment(commentId, updateReqDTO, currentMemberId);

                // then
                then(commentRepository).should().findById(commentId);
                then(commentMapper).should().updateCommentEntity(comment, updateReqDTO);
                then(commentRepository).should().save(comment);
        }

        @Test
        @DisplayName("댓글 수정 시 댓글이 존재하지 않는 경우 예외 발생")
        void updateComment_CommentNotFound_ThrowsException() {
                // given
                Long commentId = 999L;
                Long currentMemberId = 1L;
                updateReqDTO.setContent("유효한 내용"); // 검증을 통과하기 위해 유효한 내용 설정

                given(commentRepository.findById(commentId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> commentService.updateComment(commentId, updateReqDTO, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 수정 시 내용이 비어있는 경우 예외 발생")
        void updateComment_EmptyContent_ThrowsException() {
                // given
                Long commentId = 1L;
                Long currentMemberId = 1L;
                updateReqDTO.setContent("");

                // when & then
                assertThatThrownBy(() -> commentService.updateComment(commentId, updateReqDTO, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 삭제 성공")
        void deleteComment_Success() {
                // given
                Long orgId = 1L;
                Long commentId = 1L;
                Long currentMemberId = 1L;
                // Mock 객체 설정
                given(member.getMemberId()).willReturn(1L);
                given(comment.getAuthorEntity()).willReturn(member);

                given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

                // when
                commentService.deleteComment(orgId, commentId, currentMemberId);

                // then
                then(commentRepository).should().findById(commentId);
                then(commentRepository).should().delete(comment);
        }

        @Test
        @DisplayName("이슈 댓글 삭제 시 GitHub 동기화 호출")
        void deleteComment_IssueComment_CallsGitHubSync() {
                // given
                Long orgId = 1L;
                Long commentId = 1L;
                Long currentMemberId = 1L;

                // Mock 객체 설정
                given(member.getMemberId()).willReturn(currentMemberId);
                given(comment.getAuthorEntity()).willReturn(member);
                given(comment.getIssueEntity()).willReturn(issue);

                given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

                // when
                commentService.deleteComment(orgId, commentId, currentMemberId);

                // then
                then(commentRepository).should().findById(commentId);
                then(commentRepository).should().delete(comment);
        }

        @Test
        @DisplayName("댓글 삭제 시 작성자가 아닌 경우 예외 발생")
        void deleteComment_NotAuthor_ThrowsException() {
                // given
                Long orgId = 1L;
                Long commentId = 1L;
                Long differentMemberId = 999L;

                // Mock 설정: 작성자 ID는 1L, 요청자 ID는 999L로 다르게 설정
                given(member.getMemberId()).willReturn(1L);
                given(comment.getAuthorEntity()).willReturn(member);
                given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
                given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId,
                                differentMemberId, Status.ACTIVE))
                                .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> commentService.deleteComment(orgId, commentId, differentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 삭제 시 댓글이 존재하지 않는 경우 예외 발생")
        void deleteComment_CommentNotFound_ThrowsException() {
                // given
                Long orgId = 1L;
                Long commentId = 999L;
                Long currentMemberId = 1L;

                given(commentRepository.findById(commentId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> commentService.deleteComment(orgId, commentId, currentMemberId))
                                .isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("댓글 삭제 성공 - 조직 관리자 권한으로")
        void deleteComment_Success_WithAdminRole() {
                // given
                Long orgId = 1L;
                Long commentId = 1L;
                Long authorMemberId = 1L;
                Long adminMemberId = 2L;

                OrgParticipantMemberEntity adminOpm = mock(OrgParticipantMemberEntity.class);
                given(adminOpm.getRole()).willReturn(Role.ADMIN);

                // Mock 설정: 작성자 ID는 1L, 관리자 ID는 2L
                given(member.getMemberId()).willReturn(authorMemberId);
                given(comment.getAuthorEntity()).willReturn(member);
                given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
                given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId,
                                adminMemberId, Status.ACTIVE))
                                .willReturn(Optional.of(adminOpm));

                // when
                commentService.deleteComment(orgId, commentId, adminMemberId);

                // then
                then(commentRepository).should().findById(commentId);
                then(commentRepository).should().delete(comment);
                then(orgParticipantMemberRepository).should()
                                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, adminMemberId,
                                                Status.ACTIVE);
        }

        @Test
        @DisplayName("댓글 삭제 성공 - 조직 최고 관리자 권한으로")
        void deleteComment_Success_WithRootAdminRole() {
                // given
                Long orgId = 1L;
                Long commentId = 1L;
                Long authorMemberId = 1L;
                Long rootAdminMemberId = 3L;

                OrgParticipantMemberEntity rootAdminOpm = mock(OrgParticipantMemberEntity.class);
                given(rootAdminOpm.getRole()).willReturn(Role.ROOT_ADMIN);

                // Mock 설정: 작성자 ID는 1L, 최고 관리자 ID는 3L
                given(member.getMemberId()).willReturn(authorMemberId);
                given(comment.getAuthorEntity()).willReturn(member);
                given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
                given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId,
                                rootAdminMemberId, Status.ACTIVE))
                                .willReturn(Optional.of(rootAdminOpm));

                // when
                commentService.deleteComment(orgId, commentId, rootAdminMemberId);

                // then
                then(commentRepository).should().findById(commentId);
                then(commentRepository).should().delete(comment);
                then(orgParticipantMemberRepository).should()
                                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, rootAdminMemberId,
                                                Status.ACTIVE);
        }
}