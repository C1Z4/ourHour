package com.ourhour.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ourhour.domain.comment.dto.CommentCreateReqDTO;
import com.ourhour.domain.comment.dto.CommentPageResDTO;
import com.ourhour.domain.comment.dto.CommentUpdateReqDTO;
import com.ourhour.domain.comment.service.CommentService;
import com.ourhour.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 조회/등록/수정/삭제 API")
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "게시글/이슈 기준으로 댓글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<CommentPageResDTO>> getComments(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long issueId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        CommentPageResDTO response = commentService.getComments(postId, issueId, currentPage, size);

        return ResponseEntity.ok(ApiResponse.success(response, "댓글 목록 조회에 성공했습니다."));
    }

    // 댓글 등록
    @PostMapping
    @Operation(summary = "댓글 등록", description = "새 댓글을 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @Valid @RequestBody CommentCreateReqDTO commentCreateReqDTO) {

        commentService.createComment(commentCreateReqDTO);

        return ResponseEntity.ok(ApiResponse.success(null, "댓글 등록에 성공했습니다."));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable @Min(value = 1, message = "댓글 ID는 1 이상이어야 합니다.") Long commentId,
            @Valid @RequestBody CommentUpdateReqDTO commentUpdateReqDTO) {

        commentService.updateComment(commentId, commentUpdateReqDTO);

        return ResponseEntity.ok(ApiResponse.success(null, "댓글 수정에 성공했습니다."));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable @Min(value = 1, message = "댓글 ID는 1 이상이어야 합니다.") Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.ok(ApiResponse.success(null, "댓글 삭제에 성공했습니다."));
    }
}
