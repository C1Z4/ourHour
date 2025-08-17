package com.ourhour.domain.comment.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhour.domain.comment.dto.CommentCreateReqDTO;
import com.ourhour.domain.comment.dto.CommentDTO;
import com.ourhour.domain.comment.dto.CommentPageResDTO;
import com.ourhour.domain.comment.dto.CommentUpdateReqDTO;
import com.ourhour.domain.comment.service.CommentLikeService;
import com.ourhour.domain.comment.service.CommentService;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.OrgAuthority;
import com.ourhour.global.jwt.util.UserContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentController 테스트")
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private CommentLikeService commentLikeService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final Long ORG_ID = 1L;
    private static final Long MEMBER_ID = 1L;
    private static final Long COMMENT_ID = 1L;
    private static final Long POST_ID = 1L;
    private static final Long ISSUE_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
    }

    private Claims createMockClaims() {
        OrgAuthority orgAuthority = new OrgAuthority(ORG_ID, MEMBER_ID, Role.MEMBER);
        return new Claims(1L, "test@example.com", List.of(orgAuthority));
    }

    @Test
    @DisplayName("댓글 목록 조회 - 성공 (postId 기준)")
    void getComments_Success_WithPostId() throws Exception {
        // Given
        CommentDTO commentDTO = new CommentDTO(
                COMMENT_ID,
                MEMBER_ID,
                "테스트 사용자",
                "profile.jpg",
                "테스트 댓글 내용",
                LocalDateTime.now(),
                5L,
                true,
                List.of());

        CommentPageResDTO responseDTO = CommentPageResDTO.builder()
                .postId(POST_ID)
                .issueId(null)
                .comments(List.of(commentDTO))
                .currentPage(1)
                .size(10)
                .totalPages(1)
                .totalElements(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(createMockClaims());
            given(commentService.getComments(eq(POST_ID), eq(null), eq(1), eq(10), eq(MEMBER_ID)))
                    .willReturn(responseDTO);

            // When & Then
            mockMvc.perform(get("/api/org/{orgId}/comments", ORG_ID)
                    .param("postId", POST_ID.toString())
                    .param("currentPage", "1")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.postId").value(POST_ID))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments[0].commentId").value(COMMENT_ID));

            then(commentService).should().getComments(eq(POST_ID), eq(null), eq(1), eq(10), eq(MEMBER_ID));
        }
    }

    @Test
    @DisplayName("댓글 등록 - 성공")
    void createComment_Success() throws Exception {
        // Given
        CommentCreateReqDTO createReqDTO = new CommentCreateReqDTO(POST_ID, null, null, "새로운 댓글");

        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(createMockClaims());
            willDoNothing().given(commentService).createComment(any(CommentCreateReqDTO.class), eq(MEMBER_ID));

            // When & Then
            mockMvc.perform(post("/api/org/{orgId}/comments", ORG_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReqDTO)))
                    .andExpect(status().isOk());

            then(commentService).should().createComment(any(CommentCreateReqDTO.class), eq(MEMBER_ID));
        }
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void updateComment_Success() throws Exception {
        // Given
        CommentUpdateReqDTO updateReqDTO = new CommentUpdateReqDTO("수정된 댓글 내용");

        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(createMockClaims());
            willDoNothing().given(commentService).updateComment(eq(COMMENT_ID), any(CommentUpdateReqDTO.class),
                    eq(MEMBER_ID));

            // When & Then
            mockMvc.perform(put("/api/org/{orgId}/comments/{commentId}", ORG_ID, COMMENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReqDTO)))
                    .andExpect(status().isOk());

            then(commentService).should().updateComment(eq(COMMENT_ID), any(CommentUpdateReqDTO.class), eq(MEMBER_ID));
        }
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void deleteComment_Success() throws Exception {
        // Given
        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(createMockClaims());
            willDoNothing().given(commentService).deleteComment(eq(ORG_ID), eq(COMMENT_ID), eq(MEMBER_ID));

            // When & Then
            mockMvc.perform(delete("/api/org/{orgId}/comments/{commentId}", ORG_ID, COMMENT_ID))
                    .andExpect(status().isOk());

            then(commentService).should().deleteComment(eq(ORG_ID), eq(COMMENT_ID), eq(MEMBER_ID));
        }
    }

    @Test
    @DisplayName("댓글 좋아요 - 성공")
    void likeComment_Success() throws Exception {
        // Given
        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(createMockClaims());
            willDoNothing().given(commentLikeService).likeComment(eq(COMMENT_ID), eq(MEMBER_ID));

            // When & Then
            mockMvc.perform(post("/api/org/{orgId}/comments/{commentId}/like", ORG_ID, COMMENT_ID))
                    .andExpect(status().isOk());

            then(commentLikeService).should().likeComment(eq(COMMENT_ID), eq(MEMBER_ID));
        }
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - 성공")
    void unlikeComment_Success() throws Exception {
        // Given
        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(createMockClaims());
            willDoNothing().given(commentLikeService).unlikeComment(eq(COMMENT_ID), eq(MEMBER_ID));

            // When & Then
            mockMvc.perform(delete("/api/org/{orgId}/comments/{commentId}/like", ORG_ID, COMMENT_ID))
                    .andExpect(status().isOk());

            then(commentLikeService).should().unlikeComment(eq(COMMENT_ID), eq(MEMBER_ID));
        }
    }

    @Test
    @DisplayName("댓글 목록 조회 - 실패 (조직 권한 없음)")
    void getComments_Fail_NoOrgAuth() throws Exception {
        // Given
        Claims claimsWithoutOrg = new Claims(1L, "test@example.com", List.of());

        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(claimsWithoutOrg);

            // When & Then - 예외가 발생하는지 확인
            try {
                mockMvc.perform(get("/api/org/{orgId}/comments", ORG_ID)
                        .param("postId", POST_ID.toString()));
            } catch (Exception e) {
                // 예외가 발생하면 테스트 성공 (예상된 동작)
                assertThat(e).isNotNull();
                return;
            }
            // 예외가 발생하지 않으면 실패
            fail("Expected exception was not thrown");
        }
    }

    @Test
    @DisplayName("댓글 좋아요 - 실패 (조직 권한 없음)")
    void likeComment_Fail_NoOrgAuth() throws Exception {
        // Given
        Claims claimsWithoutOrg = new Claims(1L, "test@example.com", List.of());

        try (MockedStatic<UserContextHolder> mockedUserContext = Mockito.mockStatic(UserContextHolder.class)) {
            mockedUserContext.when(UserContextHolder::get).thenReturn(claimsWithoutOrg);

            // When & Then - 예외가 발생하는지 확인
            try {
                mockMvc.perform(post("/api/org/{orgId}/comments/{commentId}/like", ORG_ID, COMMENT_ID));
            } catch (Exception e) {
                // 예외가 발생하면 테스트 성공 (예상된 동작)
                assertThat(e).isNotNull();
                return;
            }
            // 예외가 발생하지 않으면 실패
            fail("Expected exception was not thrown");
        }
    }
}