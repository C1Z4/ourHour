package com.ourhour.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ourhour.domain.comment.dto.CommentPageResDTO;
import com.ourhour.domain.comment.service.CommentService;
import com.ourhour.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {    

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<CommentPageResDTO>> getComments(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long issueId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        if (postId == null && issueId == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.fail("postId 또는 issueId 중 하나는 반드시 제공되어야 합니다."));
        }

        CommentPageResDTO response = commentService.getComments(postId, issueId, currentPage, size);

        return ResponseEntity.ok(ApiResponse.success(response, "댓글 목록 조회에 성공했습니다."));
    }
}
