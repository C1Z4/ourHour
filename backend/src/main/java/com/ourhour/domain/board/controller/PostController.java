package com.ourhour.domain.board.controller;

import com.ourhour.domain.board.dto.PostDTO;
import com.ourhour.domain.board.service.PostService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.annotation.OrgId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
public class PostController {

    private final PostService postService;


    // 게시글 조회
    @GetMapping("/post")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getPostsByBoard(@OrgId @PathVariable Long postId) {
        List<PostDTO> posts = postService.findPostsByBoard(postId);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    // 게시글 상세 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<PostDTO>> getPost(@OrgId
            @PathVariable Long boardId, @PathVariable Long postId) {
        PostDTO post = postService.findPostById(postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    // 게시글 등록
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostDTO>> createPost(@OrgId
            @PathVariable Long postId,
            @RequestBody PostDTO requestDTO) {
        PostDTO createdPost = postService.createPost(postId, requestDTO);
        return new ResponseEntity<>(ApiResponse.success(createdPost), HttpStatus.CREATED);
    }


    // 게시글 수정
    @PutMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<PostDTO>> updatePost(@OrgId
            @PathVariable Long boardId, @PathVariable Long postId, @RequestBody PostDTO requestDTO) {
        PostDTO updatedPost = postService.updatePost(postId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPost));
    }

    // 게시글 삭제
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@OrgId
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
