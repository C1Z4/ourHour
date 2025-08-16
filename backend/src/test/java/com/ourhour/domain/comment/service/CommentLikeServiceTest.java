package com.ourhour.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.entity.CommentLikeEntity;
import com.ourhour.domain.comment.entity.CommentLikeId;
import com.ourhour.domain.comment.exception.CommentException;
import com.ourhour.domain.comment.repository.CommentLikeRepository;
import com.ourhour.domain.comment.repository.CommentRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentLikeService 테스트")
class CommentLikeServiceTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentLikeService commentLikeService;

    private CommentEntity comment;
    private MemberEntity member;
    private CommentLikeId commentLikeId;
    private CommentLikeEntity commentLike;

    @BeforeEach
    void setUp() {
        comment = CommentEntity.builder()
                .commentId(1L)
                .content("테스트 댓글")
                .build();

        member = MemberEntity.builder()
                .name("테스트 사용자")
                .email("test@example.com")
                .build();

        commentLikeId = new CommentLikeId(1L, 1L);

        commentLike = CommentLikeEntity.builder()
                .commentLikeId(commentLikeId)
                .commentEntity(comment)
                .memberEntity(member)
                .build();
    }

    @Test
    @DisplayName("댓글 좋아요 성공")
    void likeComment_Success() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(commentLikeRepository.existsById(commentLikeId)).willReturn(false);
        given(commentLikeRepository.save(any(CommentLikeEntity.class))).willReturn(commentLike);

        // when
        commentLikeService.likeComment(commentId, memberId);

        // then
        then(commentRepository).should().findById(commentId);
        then(memberRepository).should().findById(memberId);
        then(commentLikeRepository).should().existsById(commentLikeId);
        then(commentLikeRepository).should().save(any(CommentLikeEntity.class));
    }

    @Test
    @DisplayName("댓글 좋아요 시 댓글이 존재하지 않는 경우 예외 발생")
    void likeComment_CommentNotFound_ThrowsException() {
        // given
        Long commentId = 999L;
        Long memberId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentLikeService.likeComment(commentId, memberId))
                .isInstanceOf(CommentException.class);

        then(memberRepository).shouldHaveNoInteractions();
        then(commentLikeRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("댓글 좋아요 시 회원이 존재하지 않는 경우 예외 발생")
    void likeComment_MemberNotFound_ThrowsException() {
        // given
        Long commentId = 1L;
        Long memberId = 999L;

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentLikeService.likeComment(commentId, memberId))
                .isInstanceOf(MemberException.class);

        then(commentLikeRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("댓글 좋아요 시 이미 좋아요를 누른 경우 예외 발생")
    void likeComment_AlreadyLiked_ThrowsException() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(commentLikeRepository.existsById(commentLikeId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> commentLikeService.likeComment(commentId, memberId))
                .isInstanceOf(CommentException.class);

        then(commentLikeRepository).should().existsById(commentLikeId);
        then(commentLikeRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("댓글 좋아요 취소 성공")
    void unlikeComment_Success() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentRepository.existsById(commentId)).willReturn(true);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(commentLikeRepository.existsById(commentLikeId)).willReturn(true);

        // when
        commentLikeService.unlikeComment(commentId, memberId);

        // then
        then(commentRepository).should().existsById(commentId);
        then(memberRepository).should().existsById(memberId);
        then(commentLikeRepository).should().existsById(commentLikeId);
        then(commentLikeRepository).should().deleteById(commentLikeId);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 댓글이 존재하지 않는 경우 예외 발생")
    void unlikeComment_CommentNotFound_ThrowsException() {
        // given
        Long commentId = 999L;
        Long memberId = 1L;

        given(commentRepository.existsById(commentId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> commentLikeService.unlikeComment(commentId, memberId))
                .isInstanceOf(CommentException.class);

        then(memberRepository).shouldHaveNoInteractions();
        then(commentLikeRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 회원이 존재하지 않는 경우 예외 발생")
    void unlikeComment_MemberNotFound_ThrowsException() {
        // given
        Long commentId = 1L;
        Long memberId = 999L;

        given(commentRepository.existsById(commentId)).willReturn(true);
        given(memberRepository.existsById(memberId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> commentLikeService.unlikeComment(commentId, memberId))
                .isInstanceOf(MemberException.class);

        then(commentLikeRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 좋아요가 존재하지 않는 경우 예외 발생")
    void unlikeComment_LikeNotFound_ThrowsException() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentRepository.existsById(commentId)).willReturn(true);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(commentLikeRepository.existsById(commentLikeId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> commentLikeService.unlikeComment(commentId, memberId))
                .isInstanceOf(CommentException.class);

        then(commentLikeRepository).should().existsById(commentLikeId);
        then(commentLikeRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("댓글 좋아요 수 조회")
    void getLikeCount() {
        // given
        Long commentId = 1L;
        Long expectedCount = 5L;

        given(commentLikeRepository.countByCommentId(commentId)).willReturn(expectedCount);

        // when
        Long actualCount = commentLikeService.getLikeCount(commentId);

        // then
        assertThat(actualCount).isEqualTo(expectedCount);
        then(commentLikeRepository).should().countByCommentId(commentId);
    }

    @Test
    @DisplayName("특정 사용자가 댓글에 좋아요를 눌렀는지 확인 - 좋아요를 누른 경우")
    void isLikedByMember_LikedByUser_ReturnsTrue() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentLikeRepository.existsById(commentLikeId)).willReturn(true);

        // when
        boolean isLiked = commentLikeService.isLikedByMember(commentId, memberId);

        // then
        assertThat(isLiked).isTrue();
        then(commentLikeRepository).should().existsById(commentLikeId);
    }

    @Test
    @DisplayName("특정 사용자가 댓글에 좋아요를 눌렀는지 확인 - 좋아요를 누르지 않은 경우")
    void isLikedByMember_NotLikedByUser_ReturnsFalse() {
        // given
        Long commentId = 1L;
        Long memberId = 1L;

        given(commentLikeRepository.existsById(commentLikeId)).willReturn(false);

        // when
        boolean isLiked = commentLikeService.isLikedByMember(commentId, memberId);

        // then
        assertThat(isLiked).isFalse();
        then(commentLikeRepository).should().existsById(commentLikeId);
    }

    @Test
    @DisplayName("좋아요 수 조회 시 0개인 경우")
    void getLikeCount_ZeroLikes() {
        // given
        Long commentId = 1L;
        Long expectedCount = 0L;

        given(commentLikeRepository.countByCommentId(commentId)).willReturn(expectedCount);

        // when
        Long actualCount = commentLikeService.getLikeCount(commentId);

        // then
        assertThat(actualCount).isEqualTo(0L);
        then(commentLikeRepository).should().countByCommentId(commentId);
    }
}