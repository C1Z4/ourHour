package com.ourhour.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ourhour.domain.comment.entity.CommentLikeEntity;
import com.ourhour.domain.comment.entity.CommentLikeId;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentLikeRepository 테스트")
class CommentLikeRepositoryTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Test
    @DisplayName("댓글 좋아요 저장")
    void save() {
        // given
        Long commentId = 1L;
        Long memberId = 2L;
        CommentLikeId likeId = new CommentLikeId(commentId, memberId);
        CommentLikeEntity commentLike = CommentLikeEntity.builder()
                .commentLikeId(likeId)
                .build();

        given(commentLikeRepository.save(commentLike)).willReturn(commentLike);

        // when
        CommentLikeEntity savedLike = commentLikeRepository.save(commentLike);

        // then
        assertThat(savedLike.getCommentLikeId().getCommentId()).isEqualTo(commentId);
        assertThat(savedLike.getCommentLikeId().getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("특정 댓글의 좋아요 수 조회")
    void countByCommentId() {
        // given
        Long commentId = 1L;
        Long expectedCount = 3L;

        given(commentLikeRepository.countByCommentId(commentId)).willReturn(expectedCount);

        // when
        Long likeCount = commentLikeRepository.countByCommentId(commentId);

        // then
        assertThat(likeCount).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("좋아요가 없는 댓글의 좋아요 수 조회")
    void countByCommentId_NoLikes() {
        // given
        Long commentId = 1L;
        Long expectedCount = 0L;

        given(commentLikeRepository.countByCommentId(commentId)).willReturn(expectedCount);

        // when
        Long likeCount = commentLikeRepository.countByCommentId(commentId);

        // then
        assertThat(likeCount).isEqualTo(0);
    }

    @Test
    @DisplayName("여러 댓글의 좋아요 수를 한번에 조회")
    void countByCommentIds() {
        // given
        List<Long> commentIds = List.of(1L, 2L);
        List<Object[]> expectedResults = Arrays.asList(
                new Object[]{1L, 2L}, // commentId: 1L, count: 2L
                new Object[]{2L, 1L}  // commentId: 2L, count: 1L
        );

        given(commentLikeRepository.countByCommentIds(commentIds)).willReturn(expectedResults);

        // when
        List<Object[]> results = commentLikeRepository.countByCommentIds(commentIds);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0)[0]).isEqualTo(1L); // commentId
        assertThat(results.get(0)[1]).isEqualTo(2L); // count
        assertThat(results.get(1)[0]).isEqualTo(2L); // commentId
        assertThat(results.get(1)[1]).isEqualTo(1L); // count
    }

    @Test
    @DisplayName("특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인 - 존재하는 경우")
    void existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId_Exists() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentLikeRepository.existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId))
                .willReturn(true);

        // when
        boolean exists = commentLikeRepository.existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인 - 존재하지 않는 경우")
    void existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId_NotExists() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentLikeRepository.existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId))
                .willReturn(false);

        // when
        boolean exists = commentLikeRepository.existsByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("특정 사용자의 댓글 좋아요 조회 - 존재하는 경우")
    void findByCommentLikeId_CommentIdAndCommentLikeId_MemberId_Found() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;
        CommentLikeId likeId = new CommentLikeId(commentId, memberId);
        CommentLikeEntity commentLike = CommentLikeEntity.builder()
                .commentLikeId(likeId)
                .build();

        given(commentLikeRepository.findByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId))
                .willReturn(Optional.of(commentLike));

        // when
        Optional<CommentLikeEntity> result = commentLikeRepository
                .findByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCommentLikeId()).isEqualTo(likeId);
    }

    @Test
    @DisplayName("특정 사용자의 댓글 좋아요 조회 - 존재하지 않는 경우")
    void findByCommentLikeId_CommentIdAndCommentLikeId_MemberId_NotFound() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentLikeRepository.findByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId))
                .willReturn(Optional.empty());

        // when
        Optional<CommentLikeEntity> result = commentLikeRepository
                .findByCommentLikeId_CommentIdAndCommentLikeId_MemberId(commentId, memberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("댓글 좋아요 삭제")
    void delete() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;
        CommentLikeId likeId = new CommentLikeId(commentId, memberId);

        given(commentLikeRepository.existsById(likeId)).willReturn(false);

        // when
        commentLikeRepository.deleteById(likeId);

        // then
        verify(commentLikeRepository).deleteById(likeId);
        
        boolean exists = commentLikeRepository.existsById(likeId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("댓글 좋아요 존재 확인 - 존재하는 경우")
    void existsById_Exists() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;
        CommentLikeId likeId = new CommentLikeId(commentId, memberId);

        given(commentLikeRepository.existsById(likeId)).willReturn(true);

        // when
        boolean exists = commentLikeRepository.existsById(likeId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("댓글 좋아요 존재 확인 - 존재하지 않는 경우")
    void existsById_NotExists() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;
        CommentLikeId likeId = new CommentLikeId(commentId, memberId);

        given(commentLikeRepository.existsById(likeId)).willReturn(false);

        // when
        boolean exists = commentLikeRepository.existsById(likeId);

        // then
        assertThat(exists).isFalse();
    }
}