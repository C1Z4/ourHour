package com.ourhour.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.comment.entity.CommentEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentRepository 테스트")
class CommentRepositoryTest {

    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("게시글의 최상위 댓글 페이징 조회")
    void findByPostIdAndParentCommentIdIsNull() {
        // given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        CommentEntity comment1 = CommentEntity.builder()
                .commentId(1L)
                .content("첫 번째 댓글")
                .parentCommentId(null)
                .build();
        
        CommentEntity comment2 = CommentEntity.builder()
                .commentId(2L)
                .content("두 번째 댓글")
                .parentCommentId(null)
                .build();
        
        List<CommentEntity> comments = Arrays.asList(comment1, comment2);
        Page<CommentEntity> commentPage = new PageImpl<>(comments, pageable, comments.size());
        
        given(commentRepository.findByPostIdAndParentCommentIdIsNull(postId, pageable))
                .willReturn(commentPage);

        // when
        Page<CommentEntity> result = commentRepository.findByPostIdAndParentCommentIdIsNull(postId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(CommentEntity::getContent)
                .containsExactly("첫 번째 댓글", "두 번째 댓글");
        assertThat(result.getContent())
                .allMatch(comment -> comment.getParentCommentId() == null);
    }

    @Test
    @DisplayName("이슈의 최상위 댓글 페이징 조회")
    void findByIssueIdAndParentCommentIdIsNull() {
        // given
        Long issueId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        CommentEntity comment1 = CommentEntity.builder()
                .commentId(3L)
                .content("첫 번째 이슈 댓글")
                .parentCommentId(null)
                .build();
        
        CommentEntity comment2 = CommentEntity.builder()
                .commentId(4L)
                .content("두 번째 이슈 댓글")
                .parentCommentId(null)
                .build();

        CommentEntity githubComment = CommentEntity.builder()
                .commentId(8L)
                .content("GitHub 동기화 댓글")
                .parentCommentId(null)
                .githubId(123456L)
                .build();
        
        List<CommentEntity> comments = Arrays.asList(comment1, comment2, githubComment);
        Page<CommentEntity> commentPage = new PageImpl<>(comments, pageable, comments.size());
        
        given(commentRepository.findByIssueIdAndParentCommentIdIsNull(issueId, pageable))
                .willReturn(commentPage);

        // when
        Page<CommentEntity> result = commentRepository.findByIssueIdAndParentCommentIdIsNull(issueId, pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(CommentEntity::getContent)
                .containsExactly("첫 번째 이슈 댓글", "두 번째 이슈 댓글", "GitHub 동기화 댓글");
        assertThat(result.getContent())
                .allMatch(comment -> comment.getParentCommentId() == null);
    }

    @Test
    @DisplayName("게시글의 특정 최상위 댓글과 대댓글들 조회")
    void findByPostIdAndParentCommentIds() {
        // given
        Long postId = 1L;
        List<Long> parentCommentIds = List.of(1L);
        
        CommentEntity parentComment = CommentEntity.builder()
                .commentId(1L)
                .content("첫 번째 댓글")
                .parentCommentId(null)
                .build();
        
        CommentEntity childComment1 = CommentEntity.builder()
                .commentId(5L)
                .content("자식댓글1")
                .parentCommentId(1L)
                .build();
        
        CommentEntity childComment2 = CommentEntity.builder()
                .commentId(6L)
                .content("자식댓글2")
                .parentCommentId(1L)
                .build();
        
        List<CommentEntity> comments = Arrays.asList(parentComment, childComment1, childComment2);
        
        given(commentRepository.findByPostIdAndParentCommentIds(postId, parentCommentIds))
                .willReturn(comments);

        // when
        List<CommentEntity> result = commentRepository.findByPostIdAndParentCommentIds(postId, parentCommentIds);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(CommentEntity::getContent)
                .containsExactly("첫 번째 댓글", "자식댓글1", "자식댓글2");
    }

    @Test
    @DisplayName("이슈의 특정 최상위 댓글과 대댓글들 조회")
    void findByIssueIdAndParentCommentIds() {
        // given
        Long issueId = 1L;
        List<Long> parentCommentIds = List.of(3L);
        
        CommentEntity parentComment = CommentEntity.builder()
                .commentId(3L)
                .content("첫 번째 이슈 댓글")
                .parentCommentId(null)
                .build();
        
        CommentEntity childComment = CommentEntity.builder()
                .commentId(7L)
                .content("이슈자식댓글1")
                .parentCommentId(3L)
                .build();
        
        List<CommentEntity> comments = Arrays.asList(parentComment, childComment);
        
        given(commentRepository.findByIssueIdAndParentCommentIds(issueId, parentCommentIds))
                .willReturn(comments);

        // when
        List<CommentEntity> result = commentRepository.findByIssueIdAndParentCommentIds(issueId, parentCommentIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CommentEntity::getContent)
                .containsExactly("첫 번째 이슈 댓글", "이슈자식댓글1");
    }

    @Test
    @DisplayName("GitHub ID로 이슈 댓글 조회")
    void findByIssueEntity_IssueIdAndGithubId() {
        // given
        Long issueId = 1L;
        Long githubId = 123456L;
        
        CommentEntity comment = CommentEntity.builder()
                .commentId(8L)
                .content("GitHub 동기화 댓글")
                .githubId(githubId)
                .build();
        
        given(commentRepository.findByIssueEntity_IssueIdAndGithubId(issueId, githubId))
                .willReturn(Optional.of(comment));

        // when
        Optional<CommentEntity> result = commentRepository.findByIssueEntity_IssueIdAndGithubId(issueId, githubId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("GitHub 동기화 댓글");
        assertThat(result.get().getGithubId()).isEqualTo(githubId);
    }

    @Test
    @DisplayName("존재하지 않는 GitHub ID로 조회시 빈 결과 반환")
    void findByIssueEntity_IssueIdAndGithubId_NotFound() {
        // given
        Long issueId = 1L;
        Long nonExistentGithubId = 999999L;
        
        given(commentRepository.findByIssueEntity_IssueIdAndGithubId(issueId, nonExistentGithubId))
                .willReturn(Optional.empty());

        // when
        Optional<CommentEntity> result = commentRepository.findByIssueEntity_IssueIdAndGithubId(issueId, nonExistentGithubId);

        // then
        assertThat(result).isEmpty();
    }
}